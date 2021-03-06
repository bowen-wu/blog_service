package com.blog.controller;

import com.blog.entity.AuthResult;
import com.blog.entity.User;
import com.blog.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    UserService userService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    AuthenticationManager authenticationManager;

    @Inject
    public AuthController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/auth")
    @ResponseBody
    public AuthResult auth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication == null ? null : authentication.getName());
        if (user == null) {
            return AuthResult.success("", false, null);
        }

        return AuthResult.success("", true, user);
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public AuthResult login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return AuthResult.failure("???????????????");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);

            return AuthResult.success("????????????", true, userService.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            return AuthResult.failure("???????????????");
        }
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public AuthResult register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || password == null) {
            return AuthResult.failure("?????????|????????????");
        }
        if (username.length() < 1 || username.length() > 15) {
            return AuthResult.failure("??????????????? 1 - 15 ?????????");
        }
        if (password.length() < 6 || password.length() > 16) {
            return AuthResult.failure("???????????? 6 - 16 ?????????");
        }

        try {
            String encodePassword = bCryptPasswordEncoder.encode(password);
            User user = new User(null, username, encodePassword, "", Instant.now(), Instant.now());
            userService.register(user);
            login(params);
            return AuthResult.success("????????????", false, user);
        } catch (DuplicateKeyException e) {
            return AuthResult.failure("????????????????????????");
        }
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public AuthResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication == null ? null : authentication.getName());
        if (user == null) {
            return AuthResult.failure("??????????????????");
        }
        SecurityContextHolder.clearContext();
        return AuthResult.success("????????????", false);
    }
}
