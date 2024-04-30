package ru.kata.spring.bootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.service.UserDetailsServiceImpl;
import ru.kata.spring.bootstrap.service.UserValidator;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class AdminController {

    private UserDetailsServiceImpl userService;
    private UserValidator userValidator;

    @Autowired
    public AdminController(UserDetailsServiceImpl userService,
                           UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping("/admin")
    public String adminPage(Model model, Principal principal) {
        User thisUser = userService.findByUsername(principal.getName());
        model.addAttribute("thisUser", thisUser);
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", userService.getRoles());
        model.addAttribute("allUsers", userService.getUsers());
        return "admin/admin_panel";
    }

    @GetMapping("/admin/users")
    public String listAllUsers(Model model) {
        List<User> list = userService.getUsers();
        model.addAttribute("users", list);
        return "admin/users";
    }

    @GetMapping("/admin/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", userService.getRoles());
        return "admin/edit";
    }

    @PostMapping("/admin/edit")
    public String editUser(@RequestParam("id") Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", userService.getRoles());
        return "admin/edit";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user) {
        System.out.println(user);
        if (user.getRoles().isEmpty()) {
            user.addRole(userService.findRoleByName("ROLE_USER"));
        }
            if (!userService.update(user)) {
            System.out.println("Пользователь не был сохранен");
            return "redirect:/admin";
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, Authentication autentication, HttpSession session) {
        User deletedUser = userService.findUserById(id);
        userService.deleteUserById(id);
        if(deletedUser.getUsername().equals(autentication.getName())){
            session.invalidate();
            return "redirect:/";
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin/truncate")
    public String truncate() {
        userService.truncate();
        return "redirect:/";
    }
}