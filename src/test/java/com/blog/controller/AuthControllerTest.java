package com.blog.controller;

import com.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private Map<String, String> usernamePassword;
    private final String testUsername = "testUsername";
    private final String testPassword = "testPassword";
    private final com.blog.entity.User user = new com.blog.entity.User(1, testUsername, bCryptPasswordEncoder.encode(testPassword), "", Instant.now(), Instant.now());

    @Mock
    UserService userService;
    @Mock
    AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userService, bCryptPasswordEncoder, authenticationManager)).build();
        usernamePassword = new HashMap<>();
        usernamePassword.put("username", testUsername);
        usernamePassword.put("password", testPassword);
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("isLogin\":false")));
    }

    @Test
    void testLoginAndLogout() throws Exception {
        // 未登录时，/auth 接口返回未登录状态
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains("isLogin\":false")));

        // 使用 /auth/login 登录
        Mockito.when(userService.loadUserByUsername(testUsername))
                .thenReturn(new User(testUsername, bCryptPasswordEncoder.encode(testPassword), Collections.emptyList()));

        MvcResult mvcResult = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("登录成功")))
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession();

        // 检查 /auth 的返回值，处于登录状态
        Mockito.when(userService.getUserByUsername(testUsername)).thenReturn(user);
        mvc.perform(get("/auth").session((MockHttpSession) Objects.requireNonNull(session))).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString().contains(testUsername)));

        // 使用 /auth/logout 登出
        mvc.perform(get("/auth/logout")).andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("注销成功")));
    }

    @Test
    void testRegisterRule() throws Exception {
        usernamePassword.put("username", null);
        usernamePassword.put("password", testPassword);
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("用户名|密码为空")));

        usernamePassword.put("username", testUsername);
        usernamePassword.put("password", null);
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("用户名|密码为空")));

        usernamePassword.put("username", "");
        usernamePassword.put("password", testPassword);
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("用户名长度 1 - 15 个字符")));

        usernamePassword.put("username", "usernameOverflow");
        usernamePassword.put("password", testPassword);
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("用户名长度 1 - 15 个字符")));

        usernamePassword.put("username", testUsername);
        usernamePassword.put("password", "12345");
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("密码长度 6 - 16 个字符")));

        usernamePassword.put("username", testUsername);
        usernamePassword.put("password", "passwordOverflowSixteen");
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("密码长度 6 - 16 个字符")));
    }

    @Test
    void repeatRegister() {

    }

    @Test
    void testRegister() throws Exception {
        Mockito.when(userService.loadUserByUsername(testUsername)).thenReturn(new User(testUsername, bCryptPasswordEncoder.encode(testPassword), Collections.emptyList()));

        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("注册成功")));

        // TODO: 注册成功之后 /auth 返回登录状态
    }

    @Test
    void testLogoutWhenNotLogin() throws Exception {
        mvc.perform(get("/auth/logout")).andExpect(result -> Assertions.assertTrue(result.getResponse().getContentAsString(UTF_8).contains("用户尚未登录")));
    }
}
