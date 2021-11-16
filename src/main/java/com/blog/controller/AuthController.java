package com.blog.controller;

import com.blog.entity.AuthResponse;
import com.blog.entity.Response;
import com.blog.entity.User;
import com.blog.service.AuthService;
import com.blog.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;

@RestController
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Inject
    public AuthController(AuthService authService, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authService = authService;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || password == null) {
            return Response.failure("用户名|密码为空");
        }
        if (username.length() < 1 || username.length() > 15) {
            return Response.failure("用户名长度 1 - 15 个字符");
        }
        if (password.length() < 6 || password.length() > 16) {
            return Response.failure("密码长度 6 - 16 个字符");
        }

        try {
            String encodePassword = bCryptPasswordEncoder.encode(password);
            User user = new User(null, username, encodePassword, "", Instant.now(), Instant.now());
            userService.register(user);
            login(params);
            return AuthResponse.success("注册成功", false, user);
        } catch (DuplicateKeyException e) {
            return Response.failure("该用户名已经注册");
        }
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public Response logout() {
        return authService.logout();
    }
}
