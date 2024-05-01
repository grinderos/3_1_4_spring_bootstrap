package ru.kata.spring.bootstrap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.kata.spring.bootstrap.model.Role;

import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

    @Autowired
    public SecurityServiceImpl(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        System.out.println("--------\nпроверка аутентификации " + authentication.getName() + "\n-------\n" + authentication.isAuthenticated() + "\n-------");
        System.out.println();
        return authentication.isAuthenticated();
    }

    @Override
    public void autoLogin(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        setAuthentication(userDetails, username, password);
    }

    public void setAuthenticationAfterChangeUsername(UserDetails userDetails) {
        setAuthentication(userDetails, userDetails.getUsername(), userDetails.getPassword());
    }


    public void setAuthentication(UserDetails userDetails, String username, String password) {
        System.out.println("начало setAuthentication\n");
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        System.out.println("перед authenticationManager.authenticate(token);\n");
        authenticationManager.authenticate(token);
        System.out.println("\nзавершена передача токена в менеджер\n");
        if (token.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(token);
            System.out.println(">>>>>>>>>>>>>>>\nТокен загружен в КонтекстХолдер\n<<<<<<<<<<<<<<<");
            logger.debug(String.format("Успешный автоматический вход '%s'!", username));
        }
    }
}
