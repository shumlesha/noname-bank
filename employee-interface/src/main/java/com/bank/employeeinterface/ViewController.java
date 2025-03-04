package com.bank.employeeinterface;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @GetMapping("/accounts")
    public String accountsPage() {
        return "accounts";
    }
    
    @GetMapping("/users")
    public String usersPage() {
        return "users";
    }
    
    @GetMapping("/client/{clientId}")
    public String clientDetailsPage(@PathVariable String clientId) {
        return "client-details";
    }
    
    @GetMapping("/credit-tariffs")
    public String creditTariffsPage() {
        return "credit-tariffs";
    }

    @GetMapping("/")
    public String indexPage() {
        return "redirect:/employee/home";
    }
}
