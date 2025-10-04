package com.amouri_dev.talksy.controllers;

import com.amouri_dev.talksy.core.services.UserService;
import com.amouri_dev.talksy.entities.user.User;
import com.amouri_dev.talksy.entities.user.request.UpdatePasswordRequest;
import com.amouri_dev.talksy.entities.user.request.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PatchMapping("/me/update-profile")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void udpateProfileInfo(
            @RequestBody @Valid final UpdateProfileRequest request,
            final Authentication principal
            ) {
        this.userService.updateProfileInfo(getUserId(principal), request);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PatchMapping("/me/update-password")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updatedPassword(
            @RequestBody @Valid final UpdatePasswordRequest request,
            final Authentication principal
    ) {
        this.userService.changePassword(getUserId(principal), request);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PatchMapping("/me/deactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deactivate(final Authentication principal) {
        this.userService.deactivateAccount(getUserId(principal));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PatchMapping("/me/reactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void reactivate(final Authentication principal) {
        this.userService.reactivateAccount(getUserId(principal));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/me/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(final Authentication principal) {
        this.userService.deleteAccount(getUserId(principal));
    }

    private Long getUserId(Authentication principal) {
        return ((User) principal.getPrincipal()).getId();
    }
}
