package com.amouri_dev.talksy.core.services;

import com.amouri_dev.talksy.core.Iservices.IUserService;
import com.amouri_dev.talksy.entities.role.Role;
import com.amouri_dev.talksy.entities.user.User;
import com.amouri_dev.talksy.core.mappers.UserMapper;
import com.amouri_dev.talksy.entities.user.UserResponse;
import com.amouri_dev.talksy.entities.user.request.UpdatePasswordRequest;
import com.amouri_dev.talksy.entities.user.request.UpdateProfileRequest;
import com.amouri_dev.talksy.exception.BusinessException;
import com.amouri_dev.talksy.exception.ErrorCode;
import com.amouri_dev.talksy.infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Override
    public void updateProfileInfo(Long userId, UpdateProfileRequest request) {
        final User user = this.findUserById(userId);
        this.mapper.mergeUserInfo(user, request);
        this.userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, UpdatePasswordRequest request) {
        final User user = this.findUserById(userId);
        if (!this.passwordEncoder.matches(request.getOldPassword(),
                user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }
        final String encodedPassword = this.passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        this.userRepository.save(user);
    }

    @Override
    public void deactivateAccount(Long userId) {
        final User user = this.findUserById(userId);
        if (!user.isLocked()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }
        user.setLocked(true);
        this.userRepository.save(user);
    }

    @Override
    public void reactivateAccount(Long userId) {
        final User user = this.findUserById(userId);
        if (user.isLocked()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_ACTIVATED);
        }
        user.setLocked(false);
        this.userRepository.save(user);
    }

    @Override
    public void deleteAccount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.delete(user);
        } else {
            throw new EntityNotFoundException("User with ID " + userId + " not found");
        }
    }

    @Override
    public List<UserResponse> getAllUsersExceptSelf(Authentication authentication) {
        return userRepository.findAllUsersExceptSelf(authentication.getName())
                .stream()
                .map(user -> mapper.toUserResponse(user))
                .toList()
                ;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found."));

        log.info("User Role: {}", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return user;
    }

    private User findUserById(Long userId) {
        return this.userRepository.findUserById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
    }
}
