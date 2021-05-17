package com.tosuncu.springsecurityjwtplatformapp.listener;

import com.tosuncu.springsecurityjwtplatformapp.domain.User;
import com.tosuncu.springsecurityjwtplatformapp.domain.UserPrincipal;
import com.tosuncu.springsecurityjwtplatformapp.service.LoginAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSucces(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal){
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
            log.info("onAuthenticationSucces","kullanıcı cacheden çıkarıldı"+user.getUsername());
        }
    }
}
