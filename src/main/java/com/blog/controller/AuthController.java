package com.blog.controller;

import com.blog.entity.AuthResponse;
import com.blog.entity.ResponseStatus;
import com.blog.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class AuthController {
    private final AuthService authService;

    @Inject
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth")
    @ResponseBody
    public AuthResponse auth() {
        return this.authService.getLoginStatus();
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public AuthResponse login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        return this.authService.login(username, password);
    }
}
