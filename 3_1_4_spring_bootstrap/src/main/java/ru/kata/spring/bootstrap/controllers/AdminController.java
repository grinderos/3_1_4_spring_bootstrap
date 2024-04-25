package ru.kata.spring.bootstrap.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminController {

    @GetMapping("admin")
    public String adminPage() {
        return "admin/home";
    }
}
