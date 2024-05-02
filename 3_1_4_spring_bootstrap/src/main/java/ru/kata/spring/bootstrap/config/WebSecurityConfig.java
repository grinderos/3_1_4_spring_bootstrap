package ru.kata.spring.bootstrap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.bootstrap.service.PasswordEncoder;

@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    protected void configure(HttpSecurity http) throws Exception {
        http
//                .csrf().disable() //отключаем защиту от межсайтовой подделки запросов
                .authorizeRequests()
                .antMatchers("/", "/start", "/login", "/auth/**", "/error", "/fillUsers", "/fillRoles", "/truncate","/logout")
                .permitAll()
                .antMatchers("/admin", "/update", "/delete", "/new").hasRole("ADMIN")
                .antMatchers("/check","/user").hasAnyRole("USER","ADMIN")

                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(successUserHandler)
                .loginPage("/login")
                .loginProcessingUrl("/process_login")
//                .successForwardUrl("/check")
                .permitAll()
                .and()
                .logout()
//                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(PasswordEncoder.bCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }
}