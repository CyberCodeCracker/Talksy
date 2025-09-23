package com.amouri_dev.talksy.core.Iservices;

import com.amouri_dev.talksy.entities.user.auth.AuthenticationRequest;
import com.amouri_dev.talksy.entities.user.auth.AuthenticationResponse;
import com.amouri_dev.talksy.entities.user.auth.RefreshRequest;
import com.amouri_dev.talksy.entities.user.auth.RegistrationRequest;

public interface IAuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);
    void register(RegistrationRequest request);
    AuthenticationResponse refreshToken(RefreshRequest request);
}
