package com.amouri_dev.talksy.core.Iservices;

import com.amouri_dev.talksy.entities.user.UserResponse;
import com.amouri_dev.talksy.entities.user.request.UpdatePasswordRequest;
import com.amouri_dev.talksy.entities.user.request.UpdateProfileRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {

    void updateProfileInfo(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, UpdatePasswordRequest request);

    void deactivateAccount(Long userId);

    void reactivateAccount(Long userId);

    void deleteAccount(Long userId);
    List<UserResponse> getAllUsersExceptSelf(Authentication authentication);

    UserResponse getUserByEmail(String email);

    void logout(Long userId);
}
