package ru.kata.spring.bootstrap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import ru.kata.spring.bootstrap.service.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

//    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
//    private AuthenticationManager authenticationManager;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService
//            , AuthenticationManager authenticationManager
    ) {
        this.userDetailsService = userDetailsService;
//        this.authenticationManager = authenticationManager;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/start", "/login", "/logn", "/register", "/scrypts/**").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                                .loginProcessingUrl("/logn")
                        .successForwardUrl("/admin")
                        .failureUrl("/login")
                        .permitAll()
                )
                .logout((logout) -> logout
//                        .invalidateHttpSession(true)
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())
        ;

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(PasswordEncoder.bCryptPasswordEncoder());
    }

//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(PasswordEncoder.bCryptPasswordEncoder());
//        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
//        return daoAuthenticationProvider;
//    }
}