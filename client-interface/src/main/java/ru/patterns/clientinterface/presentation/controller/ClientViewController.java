package ru.patterns.clientinterface.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class ClientViewController {
//    @GetMapping("/login")
//    public String loginPage() {
//        return "login";
//    }
//
//    @GetMapping("/register")
//    public String registerPage() {
//        return "register";
//    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/credit")
    public String accountPage() {
        return "credit";
    }
}
