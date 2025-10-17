package com.amouri_dev.talksy.core.mappers;

import com.amouri_dev.talksy.entities.user.User;
import com.amouri_dev.talksy.entities.user.UserResponse;
import com.amouri_dev.talksy.entities.user.auth.RegistrationRequest;
import com.amouri_dev.talksy.entities.user.request.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public void mergeUserInfo(User user, UpdateProfileRequest request) {
        if (StringUtils.isNotBlank(request.getFirstName())
                && !user.getFirstName().equals(request.getFirstName())) {
            user.setFirstName(request.getFirstName());
        }
        if (StringUtils.isNotBlank(request.getLastName())
                && !user.getLastName().equals(request.getLastName())) {
            user.setLastName(request.getLastName());
        }
    }

    public User toUser(RegistrationRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .nickname(request.getNickname())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .isEnabled(true)
                .isLocked(false)
                .isCredentialsExpired(false)
                .isEmailVerified(false)
                .build()
                ;
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isOnline(user.isUserOnline())
                .lastSeen(user.getLastSeen())
                .nickname(user.getNickname())
                .build()
                ;
    }
}
