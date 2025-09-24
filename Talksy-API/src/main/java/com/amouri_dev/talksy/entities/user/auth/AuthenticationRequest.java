package com.amouri_dev.talksy.entities.user.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotBlank(message = "Email is necessary.")
    @Email(message = "Please provide a correct email format.")
    @Schema(example = "souhail@gmail.com")
    private String email;
    @NotBlank(message = "Password is necessary")
    @Schema(example = "password123")
    private String password;
}
