package ru.kata.spring.bootstrap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/start").setViewName("start");
        registry.addViewController("/").setViewName("start");
//        registry.addViewController("/admin").setViewName("admin");
//        registry.addViewController("/auth").setViewName("auth");
    }

}