package com.amouri_dev.talksy.entities.user.auth;

import com.amouri_dev.talksy.validation.NonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {

    @NotBlank(message = "First name is mandatory")
    @Size(
            min = 3,
            max = 10,
            message = "First name size must be between 3 and 10."
    )
    @Schema(example = "Souhail")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    @Size(
            min = 3,
            max = 10,
            message = "Last name size must be between 3 and 10."
    )
    @Schema(example = "Amouri")
    private String lastName;
    @NotBlank(message = "Nickname is mandatory")
    @Size(
            min = 3,
            max = 15,
            message = "Nickname size must be between 3 and 15."
    )
    @Schema(example = "CoolDude")
    private String nickname;
    @NotBlank(message = "Email is necessary")
    @Email(message = "Please provide a correct email format")
    @Schema(example = "souhail@gmail.com")
    @NonDisposableEmail(message = "Please provide a valid email")
    private String email;
    @NotBlank(message = "Password is necessary")
    @Size(
            min = 8,
            max = 15,
            message = "Password size must be between 8 and 15."
    )
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Minimum eight characters, at least one letter and one number"
    )
    private String password;
}
