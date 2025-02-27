package com.bank.userservice.controller;


import com.bank.userservice.dto.api.DefaultResponse;
import com.bank.userservice.dto.user.BanUserRequest;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.security.CurrentUser;
import com.bank.userservice.service.UserService;
import com.bank.userservice.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/ban/{userId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Ban a user", description = "Ban a user by providing the user id and the ban reason")
    public ResponseEntity<DefaultResponse<UserDto>> banUser(@PathVariable UUID userId,
                                                            @RequestBody BanUserRequest banUserRequest,
                                                            @AuthenticationPrincipal CurrentUser currentUser) {
        UserDto bannedUser = userService.banUser(userId, banUserRequest, currentUser.getId());

        return ResponseEntity.ok(
                ResponseBuilder.success(
                        "User has %s been banned successfully".formatted(bannedUser.getEmail()),
                        bannedUser
                )
        );
    }


    @GetMapping("/me")
    @Operation(summary = "Get user details", description = "Get the details of the currently logged in user")
    public ResponseEntity<DefaultResponse<UserDto>> getMe(@AuthenticationPrincipal CurrentUser currentUser) {
        UserDto user = userService.getUserById(currentUser.getId());

        return ResponseEntity.ok(
                ResponseBuilder.success(
                        "User details fetched successfully",
                        user
                )
        );
    }
}
