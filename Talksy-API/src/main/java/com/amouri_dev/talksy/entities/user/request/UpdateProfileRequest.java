package com.amouri_dev.talksy.entities.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "Can't change to an empty nickname.")
    @Size(
            min = 3,
            max = 15,
            message = "Nickname size must be between 3 and 15."
    )
    private String nickname;
    private MultipartFile profilePicture;

}
