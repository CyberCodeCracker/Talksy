package com.amouri_dev.talksy.entities.user.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordRequest {

    private String oldPassword;
    private String newPassword;
}
