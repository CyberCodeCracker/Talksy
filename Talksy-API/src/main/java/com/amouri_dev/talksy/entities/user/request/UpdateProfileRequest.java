package com.amouri_dev.talksy.entities.user.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {

    private String firstName;
    private String lastName;
}
