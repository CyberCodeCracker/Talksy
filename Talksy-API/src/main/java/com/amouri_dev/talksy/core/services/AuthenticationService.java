package com.amouri_dev.talksy.core.services;

import com.amouri_dev.talksy.core.Iservices.IAuthenticationService;
import com.amouri_dev.talksy.core.Iservices.IEmailService;
import com.amouri_dev.talksy.entities.email.EmailTemplateName;
import com.amouri_dev.talksy.entities.role.Role;
import com.amouri_dev.talksy.entities.token.Token;
import com.amouri_dev.talksy.entities.token.TokenType;
import com.amouri_dev.talksy.infrastructure.RoleRepository;
import com.amouri_dev.talksy.entities.user.User;
import com.amouri_dev.talksy.entities.user.UserMapper;
import com.amouri_dev.talksy.entities.user.auth.AuthenticationRequest;
import com.amouri_dev.talksy.entities.user.auth.AuthenticationResponse;
import com.amouri_dev.talksy.entities.user.auth.RefreshRequest;
import com.amouri_dev.talksy.entities.user.auth.RegistrationRequest;
import com.amouri_dev.talksy.exception.BusinessException;
import com.amouri_dev.talksy.exception.ErrorCode;
import com.amouri_dev.talksy.infrastructure.TokenRepository;
import com.amouri_dev.talksy.infrastructure.UserRepository;
import com.amouri_dev.talksy.security.JwtService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements IAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IEmailService emailService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final UserMapper userMapper;

    @Value("${app.mail.frontend.activation-url}")
    private String activationUrl;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final User user = (User) authentication.getPrincipal();
        final String accessToken = this.jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        final String tokenType = "Bearer ";

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build()
                 ;
    }

    @Override
    @Transactional
    public void register(final RegistrationRequest request) throws MessagingException {
        checkUserEmail(request.getEmail());

        final Role userRole = this.roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Role user does not exist"));

        final Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        final User user = this.userMapper.toUser(request);
        user.setRoles(roles);
        log.debug("Saving user {}", user);
        this.userRepository.save(user);

        final List<User> users = new ArrayList<>();
        users.add(user);
        user.setRoles(roles);

        this.roleRepository.save(userRole);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String newToken = generateAndSaveActivationToken(user);

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    user.getUsername(),
                    EmailTemplateName.ACTIVATE_ACCOUNT,
                    activationUrl,
                    newToken,
                    "Account activation");
        } catch (MessagingException e) {
            System.out.println("Exception " + e.getMessage());
        }
    }


    private String generateAndSaveActivationToken(User user) {
        var generatedToken = generateActivationTokenBody(6);
        var token = Token.builder()
                .token(generatedToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .tokenType(TokenType.BEARER)
                .user(user)
                .build()
                ;
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationTokenBody(int length) {
        String charSequence = "0123456789";
        StringBuilder activationToken = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(charSequence.length());
            activationToken.append(charSequence.charAt(randomIndex));
        }
        return activationToken.toString();
    }

    @Override
    public AuthenticationResponse refreshToken(final RefreshRequest request) {
        final String newAccessToken = this.jwtService.refreshAccessToken(request.getRefreshToken());
        final String tokenType = "Bearer ";
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType(tokenType)
                .build()
                ;
    }

    private void checkUserEmail(String email) {
        final boolean exists = userRepository.findByEmail(email).isPresent();
        if (exists) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }
    }
}
