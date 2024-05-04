package ru.kata.spring.bootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public String login(Model model, String error, String logout, Authentication auth) {
        System.out.println("сработал /login");
        if (userService.getUsers().isEmpty()) {
            return "redirect:auth/register";
        }
        if (securityService.isAuthenticated()) {
            System.out.println("this user authenticated");
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return "redirect:/admin/";
            else return "redirect:/user";
        }
        if (error != null)
            model.addAttribute("error", "Имя пользователя или пароль не совпадают");
        if (logout != null)
            model.addAttribute("message", "Успешный выход из системы");
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registration(Model model, ModelMap messageModel) {
        if (userService.getRoles().isEmpty() && roles.isEmpty()) {
            userService.fillRoles();
            roles = userService.getRoles();
        }
        if (userService.getUsers().isEmpty()) {
            ArrayList<String> messages = new ArrayList<>();
            messages.add("В системе нет ни одного пользователя. Зарегистрируйтесь, чтобы стать первым.");
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
        userValidator.validateOnReg(user, bindingResult);
        if (bindingResult.hasErrors() || !userService.save(user)) {
            model.addAttribute("roles", roles);
            return "/auth/register";
        }
        securityService.autoLogin(user.getUsername(), user.getPasswordConfirm());
        if (!user.getRoles().contains(userService.findRoleByName("ROLE_ADMIN"))) {
            return "redirect:/user";
        }
        return "redirect:/admin";
    }
}
