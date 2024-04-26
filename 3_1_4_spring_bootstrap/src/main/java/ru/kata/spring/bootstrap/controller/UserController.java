package ru.kata.spring.bootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.bootstrap.model.Role;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.service.UserDetailsServiceImpl;

import java.security.Principal;


@Controller
//@RequestMapping
public class UserController {

    private UserDetailsServiceImpl userService;
    private final Role checkAdmin = new Role("ROLE_ADMIN");

    @Autowired
    public UserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String getUserInfo(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "/user";
    }

    @GetMapping("/to_main")
    public String toMain(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user.getRoles().contains(checkAdmin)) {
            return "redirect:/admin";
        } else {
            return "redirect:/user";
        }
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
}