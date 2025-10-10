package com.amouri_dev.talksy.core.Iservices;

import com.amouri_dev.talksy.entities.user.auth.AuthenticationRequest;
import com.amouri_dev.talksy.entities.user.auth.AuthenticationResponse;
import com.amouri_dev.talksy.entities.user.auth.RefreshRequest;
import com.amouri_dev.talksy.entities.user.auth.RegistrationRequest;
import jakarta.mail.MessagingException;

public interface IAuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);
    void register(RegistrationRequest request) throws MessagingException;
    AuthenticationResponse refreshToken(RefreshRequest request);
    void confirmAccount(String token) throws MessagingException;
}
