package com.amouri_dev.talksy.entities.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "Can't change to an empty first name.")
    @Size(
            min = 3,
            max = 10,
            message = "New first name size must be between 3 and 10."
    )
    private String firstName;
    @NotBlank(message = "Can't change to an empty last name.")
    @Size(
            min = 3,
            max = 10,
            message = "Last name size must be between 3 and 10."
    )
    private String lastName;
    @NotBlank(message = "Can't change to an empty nickname.")
    @Size(
            min = 3,
            max = 15,
            message = "Nickname size must be between 3 and 15."
    )
    private String nickname;
}
