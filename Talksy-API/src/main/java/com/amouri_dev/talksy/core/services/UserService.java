package com.amouri_dev.talksy.core.services;

import com.amouri_dev.talksy.core.Iservices.IUserService;
import com.amouri_dev.talksy.entities.user.User;
import com.amouri_dev.talksy.entities.user.UserMapper;
import com.amouri_dev.talksy.entities.user.request.UpdatePasswordRequest;
import com.amouri_dev.talksy.entities.user.request.UpdateProfileRequest;
import com.amouri_dev.talksy.exception.BusinessException;
import com.amouri_dev.talksy.exception.Errorcode;
import com.amouri_dev.talksy.infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
            throw new BusinessException(Errorcode.INVALID_CURRENT_PASSWORD);
        }
        final String encodedPassword = this.passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        this.userRepository.save(user);
    }

    @Override
    public void deactivateAccount(Long userId) {
        final User user = this.findUserById(userId);
        if (!user.isLocked()) {
            throw new BusinessException(Errorcode.ACCOUNT_ALREADY_DEACTIVATED);
        }
        user.setLocked(true);
        this.userRepository.save(user);
    }

    @Override
    public void reactivateAccount(Long userId) {
        final User user = this.findUserById(userId);
        if (user.isLocked()) {
            throw new BusinessException(Errorcode.ACCOUNT_ALREADY_ACTIVATED);
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found."));
    }

    private User findUserById(Long userId) {
        return this.userRepository.findUserById(userId)
                .orElseThrow(() -> new BusinessException(Errorcode.USER_NOT_FOUND, userId));
    }
}
