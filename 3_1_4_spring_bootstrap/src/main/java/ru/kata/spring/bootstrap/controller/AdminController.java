package ru.kata.spring.bootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    public String adminPage(Model model, Authentication autentication, HttpSession session) {
        System.out.println("adminPage - principal.getName(): " +autentication.getName());
        User thisUser;
        if((thisUser = userService.findByUsername(autentication.getName())) == null){
            System.out.println("\nВ БАЗЕ НЕ НАЙДЕН ЮЗЕР ИЗ АТЕНТИФИКАЦИИ");
            session.invalidate();
            return "redirect:/login";
        }
        if (!thisUser.getRoles().contains(userService.findRoleByName("ROLE_ADMIN"))) {
            return "redirect:/user";
        }
        model.addAttribute("thisUser", thisUser);
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", userService.getRoles());
        model.addAttribute("allUsers", userService.getUsers());
        return "admin/admin_panel";
    }

    @PostMapping("/admin")
    public String admin() {
        return "redirect:/admin";
    }

    @PostMapping("/new")
    public String add(@ModelAttribute("newUser") User newUser) {
        if (newUser.getRoles().isEmpty()) {
            newUser.addRole(userService.findRoleByName("ROLE_USER"));
        }
        if (!userService.save(newUser)) {
            System.out.println("пользователь с таким логином уже есть");
        }
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user, Authentication autentication, HttpSession session) {
        if (user.getRoles().isEmpty()) {
            user.addRole(userService.findRoleByName("ROLE_USER"));
        }
        if (!userService.update(user)) {
            System.out.println("Пользователь не был сохранен");
            return "redirect:/admin";
        }
        System.out.println("\nPrincipal in /update: "+autentication.getName());
        User checkUser = userService.findByUsername(autentication.getName());
        if(checkUser==null) {
            if (!user.getUsername().equals(autentication.getName())) {
                System.out.println("\nПОМЕНЯЛСЯ ЛОГИН ЮЗЕРА\n");
                System.out.println("new login: "+user.getUsername()+"\n");
                session.invalidate();
                return "redirect:/invalidate";
//                securityService.setAuthenticationAfterChangeUsername(user);
//                System.out.println("*****************************");
            }
        }
        if (!user.getRoles().contains(userService.findRoleByName("ROLE_ADMIN"))) {
            System.out.println("редирект на /user\n");
            return "redirect:/user";
        }
        System.out.println("\nМЕТОД updateUser ОТРАБОТАЛ ПОЛНОСТЬЮ\n");
        return "redirect:/admin";
    }

    @GetMapping("/invalidate")
    public String invalidate(ModelMap messageModel) {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("Вы изменили свое имя пользователя. Повторите вход.");
        messageModel.addAttribute("messages", messages);
        return "auth/login";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, Authentication autentication, HttpSession session) {
        User deletedUser = userService.findUserById(id);
        userService.deleteUserById(id);
        if (deletedUser.getUsername().equals(autentication.getName())) {
            session.invalidate();
            return "redirect:/";
        }
        return "redirect:/admin";
    }
}