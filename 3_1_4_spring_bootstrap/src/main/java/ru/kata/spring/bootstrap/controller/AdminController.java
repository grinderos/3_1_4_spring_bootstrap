package ru.kata.spring.bootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.service.SecurityServiceImpl;
import ru.kata.spring.bootstrap.service.UserDetailsServiceImpl;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
public class AdminController {

    private UserDetailsServiceImpl userService;
    private SecurityServiceImpl securityService;

    @Autowired
    public AdminController(UserDetailsServiceImpl userService,
                           SecurityServiceImpl securityService

    ) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping("/admin")
    public String adminPage(Model model, Authentication auth, HttpSession session) {
        User thisUser = userService.findByUsername(auth.getName());
//        if((thisUser = userService.findByUsername(auth.getName())) == null){
//            System.out.println("\nВ БАЗЕ НЕ НАЙДЕН ЮЗЕР ИЗ АТЕНТИФИКАЦИИ");
//            session.invalidate();
//            return "redirect:/login";
//        }
//        if (!thisUser.getRoles().contains(userService.findRoleByName("ROLE_ADMIN"))) {
//            return "redirect:/user";
//        }
        model.addAttribute("thisUser", thisUser);
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", userService.getRoles());
        model.addAttribute("allUsers", userService.getUsers());
        return "admin/admin_panel";
    }

    @PostMapping("/new")
    public String add(@ModelAttribute("newUser") User newUser) {
        if (newUser.getRoles().isEmpty()) {
            newUser.addRole(userService.findRoleByName("ROLE_USER"));
        }
        if (!userService.save(newUser)) {
            System.out.println("\nПользователь с логином '"+
                    newUser.getUsername()+"' уже существует\n");
        }
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user, Authentication auth, HttpSession session) {

        if (user.getRoles().isEmpty() &&
                user.getUsername().equals(auth.getName()) &&
                auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            user.addRole(userService.findRoleByName("ROLE_ADMIN"));
        } else if (user.getRoles().isEmpty()){
            user.addRole(userService.findRoleByName("ROLE_USER"));
        }
        if (!userService.update(user)) {
            return "redirect:/admin";
        }
        if(userService.findByUsername(auth.getName())==null) {
                session.invalidate();
                return "redirect:/login";
        }
        return auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))?
        "redirect:/admin" : "redirect:/user";
    }


    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, Authentication auth, HttpSession session) {
        User deletedUser = userService.findUserById(id);
        userService.deleteUserById(id);
        if (deletedUser.getUsername().equals(auth.getName())) {
            session.invalidate();
            return "redirect:/";
        }
        return "redirect:/admin";
    }
}