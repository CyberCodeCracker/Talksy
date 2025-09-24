package com.amouri_dev.talksy.entities.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordRequest {

    @NotBlank(message = "Old password is necessary")
    @Size(
            min = 8,
            max = 15,
            message = "Old password size must be between 8 and 15."
    )
    private String oldPassword;
    @NotBlank(message = "Password is necessary")
    @Size(
            min = 8,
            max = 15,
            message = "New password size must be between 8 and 15."
    )
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Minimum eight characters, at least one letter and one number"
    )
    private String newPassword;
}
