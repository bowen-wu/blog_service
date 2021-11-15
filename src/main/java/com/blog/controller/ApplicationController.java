package com.blog.controller;

import com.blog.entity.User;
import com.blog.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class ApplicationController {
    private UserService userService;

    @Inject
    public ApplicationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public User getUserById(@PathVariable int id) {
        return this.userService.getUserById(id);
    }
}
