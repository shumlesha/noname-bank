package com.bank.userservice.controller;


import com.bank.userservice.dto.api.DefaultResponse;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.security.CurrentUser;
import com.bank.userservice.service.UserService;
import com.bank.userservice.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/ban/{userId}")
    public ResponseEntity<DefaultResponse<UserDto>> banUser(@PathVariable UUID userId,
                                                            @AuthenticationPrincipal CurrentUser currentUser) {
        UserDto bannedUser = userService.banUser(userId, currentUser.getId());

        return ResponseEntity.ok(
                ResponseBuilder.success(
                        "User has %s been banned successfully".formatted(bannedUser.getEmail()),
                        bannedUser
                )
        );
    }
}
