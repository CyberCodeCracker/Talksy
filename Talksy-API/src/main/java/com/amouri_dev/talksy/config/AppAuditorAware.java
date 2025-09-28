 package com.amouri_dev.talksy.config;

import com.amouri_dev.talksy.entities.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AppAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication ==  null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        final User user = (User) authentication.getPrincipal();
        return Optional.ofNullable(user.getId());
    }
}
