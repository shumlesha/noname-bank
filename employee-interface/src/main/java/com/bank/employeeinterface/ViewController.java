package com.bank.employeeinterface;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class ViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "home";
    }

    @GetMapping("/me")
    public String mePage() {
        return "home";
    }

    @GetMapping("/")
    public String indexPage() {
        return "redirect:/employee/home";
    }
}
