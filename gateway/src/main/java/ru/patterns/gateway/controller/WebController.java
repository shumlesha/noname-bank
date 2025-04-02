package ru.patterns.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.patterns.gateway.service.AuthService;
import ru.patterns.gateway.service.LogoutService;
import ru.patterns.gateway.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class WebController {
    private final AuthService authService;
    private final UserService userService;
    private final LogoutService logoutService;

    @GetMapping
    public Mono<String> index(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return authService.getLoginUrl()
                    .map(url -> {
                        model.addAttribute("loginUrl", url);
                        return "index";
                    });
        }
        return Mono.just("redirect:/home");
    }

    @GetMapping("/home")
    public Mono<String> home(
            @AuthenticationPrincipal OidcUser oidcUser,
            Model model) {


        return userService.getUserInfo(oidcUser.getIdToken())
                .map(userInfo -> {
                    model.addAttribute("userInfo", userInfo);
                    model.addAttribute("roles", userInfo.getRoles());
                    model.addAttribute("hasEmployeeRole", userInfo.hasRole("EMPLOYEE"));
                    model.addAttribute("hasClientRole", userInfo.hasRole("CLIENT"));
                    return "home";
                });
    }

    @GetMapping("/logout")
    public Mono<String> logout(ServerWebExchange exchange, Authentication authentication) {
        return logoutService.logout(exchange, authentication)
                .map(logoutUri -> "redirect:" + logoutUri);
    }
}
