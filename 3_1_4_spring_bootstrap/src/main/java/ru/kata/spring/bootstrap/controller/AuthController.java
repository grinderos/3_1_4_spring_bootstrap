package ru.kata.spring.bootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.bootstrap.model.Role;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.service.SecurityService;
import ru.kata.spring.bootstrap.service.UserDetailsServiceImpl;
import ru.kata.spring.bootstrap.service.UserValidator;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {

    private UserDetailsServiceImpl userService;
    private SecurityService securityService;
    private UserValidator userValidator;
    private List<Role> roles;

    @Autowired
    public AuthController(UserDetailsServiceImpl userService,
                          SecurityService securityService,
                          UserValidator userValidator) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        roles = userService.getRoles();
    }

    @GetMapping("/")
    public String startPage() {
        return "start";
    }


    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        System.out.println("сработал /login");

        if (securityService.isAuthenticated()) {
            System.out.println("isAuthenticated() true");
            return "redirect:/admin/";
        }
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "auth/login";
    }


    @GetMapping("/auth/register")
    public String registration(Model model, ModelMap messageModel) {
        if(userService.getRoles().isEmpty()){
            userService.fillRoles();
        }
        if (userService.getUsers().isEmpty()) {
            ArrayList<String> messages = new ArrayList<>();
            messages.add("В системе нет ни одного пользователя.");
            messages.add("Зарегистрируйтесь, чтобы стать первым.");
            messageModel.addAttribute("messages", messages);
        }
        model.addAttribute("user", new User());
        model.addAttribute("roles", roles);
        return "/auth/register";
    }

    @PostMapping("/auth/register_new_user")
    public String addUser(@ModelAttribute("user") User user, Model model, BindingResult bindingResult) {
        if (user.getRoles().isEmpty()) {
            user.addRole(userService.findRoleByName("ROLE_USER"));
        }
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roles);
            System.out.println("bindingResult.hasErrors() есть ошибки");
            return "/auth/register";
        }
        if (!userService.save(user)) {
            model.addAttribute("roles", roles);
            System.out.println("Пользователь не был сохранен");
            return "/auth/register";
        }
        securityService.autoLogin(user.getUsername(), user.getPasswordConfirm());
        System.out.println("-------\nАВТОЛОГИН ОТРАБОТАЛ\n---------");
        return "redirect:/admin";
    }
}
