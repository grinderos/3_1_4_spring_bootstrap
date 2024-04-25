package ru.kata.spring.bootstrap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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

    @Qualifier("userDetailsServiceImpl")
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
                        .requestMatchers("/", "/start", "/auth/**", "/scrypts/**", "/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/auth/login")
                        .successForwardUrl("/in_system/home")
                        .failureUrl("/auth/error")
                        .permitAll()
                )
                .logout((logout) -> logout.invalidateHttpSession(true)
                        .logoutSuccessUrl("/start")
                        .permitAll())
        ;

        return http.build();
    }

    //    @Bean
//    public AuthenticationManager customAuthenticationManager() {
//        return authenticationManager;
//    }

//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(PasswordEncoder.bCryptPasswordEncoder());
    }
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails in_system =
//                User.withDefaultPasswordEncoder()
//                        .username("in_system")
//                        .password("password")
//                        .roles("USER")
//                        .build();
//
//        return new InMemoryUserDetailsManager(in_system);
//    }
}