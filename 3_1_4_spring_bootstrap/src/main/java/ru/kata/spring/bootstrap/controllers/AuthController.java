package ru.kata.spring.bootstrap.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.service.SecurityService;
import ru.kata.spring.bootstrap.service.UserService;
import ru.kata.spring.bootstrap.service.UserValidator;

@Controller
public class AuthController {

    private UserService userService;
    private SecurityService securityService;
    private UserValidator userValidator;

    @Autowired
    public AuthController(UserService userService, SecurityService securityService, UserValidator userValidator) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
    }

    @GetMapping("/")
    public String startPage() {
        return "start";
    }

//    @GetMapping("/login")
//    public String login() {
//        return "auth/login";
//    }
//
//    @RequestMapping("/error")
//    public String loginError(Model model) {
//        model.addAttribute("loginError", true);
//        return "auth/login";
//    }

    @GetMapping("auth/login")
    public String login(Model model, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return "redirect:/home-page";
        }
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "auth/login";
    }

    @GetMapping("auth/register")
    public String register(Model model) {
        if (securityService.isAuthenticated()) {
            return "redirect:/home-page";
        }
        model.addAttribute("user", new User());
        return "/auth/register";
    }

    @PostMapping("auth/register")
    public String register(@ModelAttribute("user") User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        userService.save(user);
        securityService.autoLogin(user.getUsername(), user.getPasswordConfirm());
        return "redirect:/home-page";
    }



}