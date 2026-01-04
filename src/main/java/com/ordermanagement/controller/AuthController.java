package com.ordermanagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ordermanagement.dto.LoginRequestDto;
import com.ordermanagement.entity.User;
import com.ordermanagement.security.JwtUtil;
import com.ordermanagement.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Login and generate JWT token")
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequestDto requestDto) {

        User user = userService.authenticate(
            requestDto.getUsername(),
             requestDto.getPassword()
            );

        return JwtUtil.generateToken(
            user.getUsername(),
            user.getRole().name()
        );
    }

}
