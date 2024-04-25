package ru.kata.spring.bootstrap.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.bootstrap.model.Role;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.service.SecurityService;
import ru.kata.spring.bootstrap.service.UserService;
import ru.kata.spring.bootstrap.service.UserValidator;

import java.util.List;

@Controller
public class AuthController {

    private UserService userService;
    private SecurityService securityService;
    private UserValidator userValidator;
    private List<Role> roles;

    @Autowired
    public AuthController(UserService userService,
                          SecurityService securityService,
                          UserValidator userValidator) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        roles = userService.getRoleRepository().findAll();
    }

    @GetMapping("/")
    public String startPage() {
        return "start";
    }

    @GetMapping("/login")
    public String login() {
        System.out.println(" _____ ЛОГИНКА ГЕТ ______");
        return "/login";
    }
//
//    @RequestMapping("/error")
//    public String loginError(Model model) {
//        model.addAttribute("loginError", true);
//        return "/login";
//    }

    @PostMapping("/logn")
    public String login(Model model, String error, String logout) {
        System.out.println(" _____ ЛОГИНКА ПОСТ______");
        if (securityService.isAuthenticated()) {
            System.out.println("\n-------------\n ПРОВЕРКА АУТЕНТИФИКАЦИИ ПОЛОЖИТЕЛЬНА\n-----------\n");
            return "redirect:/admin";
//            return "/admin/home";
        }
        if (error != null)
            model.addAttribute("error", "Имя пользователя или пароль неверны.");
        if (logout != null)
            model.addAttribute("message", "Вы успешно вышли из системы.");
        System.out.println("\n-------------\n ОТКАЗ логинки\n-----------\n");
        return "/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if(roles.isEmpty()){
            userService.fillRoles();
            roles = userService.getRoleRepository().findAll();
        }
        if (securityService.isAuthenticated()) {
            return "redirect:/admin";
        }
        model.addAttribute("user", new User());
        model.addAttribute("roles", roles);
        return "/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user,Model model
            , BindingResult bindingResult) {
        System.out.println(user);
        model.addAttribute("roles", roles);
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/register";
        }
        userService.save(user);
//        securityService.autoLogin(admin.getUsername(), admin.getPasswordConfirm());
        return "redirect:/admin";
    }



}