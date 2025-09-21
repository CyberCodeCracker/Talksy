package com.amouri_dev.talksy.core.Iservices;

import com.amouri_dev.talksy.entities.user.request.UpdatePasswordRequest;
import com.amouri_dev.talksy.entities.user.request.UpdateProfileRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {

    void updateProfileInfo(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, UpdatePasswordRequest request);

    void deactivateAccount(Long userId);

    void reactivateAccount(Long userId);

    void deleteAccount(Long userId);
}
