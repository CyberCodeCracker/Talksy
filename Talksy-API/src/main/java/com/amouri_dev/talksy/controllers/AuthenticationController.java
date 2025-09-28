package com.amouri_dev.talksy.controllers;

import com.amouri_dev.talksy.core.Iservices.IAuthenticationService;
import com.amouri_dev.talksy.entities.user.auth.AuthenticationRequest;
import com.amouri_dev.talksy.entities.user.auth.AuthenticationResponse;
import com.amouri_dev.talksy.entities.user.auth.RefreshRequest;
import com.amouri_dev.talksy.entities.user.auth.RegistrationRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {
    private final IAuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void register(
            @Valid @RequestBody final RegistrationRequest request
    ) throws MessagingException {
        this.authenticationService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody final AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @Valid @RequestBody final RefreshRequest request
    ) {
        return ResponseEntity.ok(this.authenticationService.refreshToken(request));
    }

}
