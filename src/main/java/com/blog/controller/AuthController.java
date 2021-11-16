package com.blog.controller;

import com.blog.entity.AuthResponse;
import com.blog.entity.Response;
import com.blog.service.AuthService;
import com.blog.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

@RestController
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Inject
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    @GetMapping("/auth")
    @ResponseBody
    public AuthResponse auth() {
        return this.authService.getLoginStatus();
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public Response login(@RequestBody Map<String, String> params) {
        return this.authService.login(params.get("username"), params.get("password"));
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public Response register(@RequestBody Map<String, String> params) {
        return this.userService.register(params.get("username"), params.get("password"));
    }
}
