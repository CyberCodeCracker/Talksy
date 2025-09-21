package com.amouri_dev.talksy.entities.user;

import com.amouri_dev.talksy.entities.user.request.UpdateProfileRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

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
}
