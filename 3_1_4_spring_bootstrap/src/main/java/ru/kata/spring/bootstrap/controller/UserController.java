package ru.kata.spring.bootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.service.UserDetailsServiceImpl;

import java.security.Principal;


@Controller
public class UserController {

    private UserDetailsServiceImpl userService;

    @Autowired
    public UserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String getUserInfo(Model model, Principal principal) {
        User thisUser = userService.findByUsername(principal.getName());
        model.addAttribute("thisUser", thisUser);
        return "/user";
    }

    @GetMapping("/fillUsers")
    public String fillUsers() {
        userService.fillUsers();
        return "redirect:/";
    }

    @GetMapping("/fillRoles")
    public String fillRoles() {
        userService.fillRoles();
        return "redirect:/";
    }
    @GetMapping("/truncate")
    public String truncate() {
        userService.truncate();
        return "redirect:/";
    }
}