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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private Map<String, String> usernamePassword;
    private final String testUsername = "testUsername";
    private final String testPassword = "testPassword";
    private final com.blog.entity.User testUser = new com.blog.entity.User(1, testUsername, bCryptPasswordEncoder.encode(testPassword), "", Instant.now(), Instant.now());

    @Mock
    UserService userService;
    @Mock
    AuthenticationManager authenticationManager;

    public ResultActions httpRequest(String api, HttpMethod method) throws Exception {
        return httpRequest(api, method, null);
    }

    public ResultActions httpRequest(String api, HttpMethod method, Object body) throws Exception {
        return httpRequest(api, method, body, null);
    }

    public ResultActions httpRequest(String api, HttpMethod method, Object body, Object session) throws Exception {
        MockHttpServletRequestBuilder request = request(method, api).contentType(MediaType.APPLICATION_JSON);
        if (body != null) {
            request.content(new ObjectMapper().writeValueAsString(body));
        }
        if (session != null) {
            request.session((MockHttpSession) Objects.requireNonNull(session));
        }
        return mvc.perform(request).andExpect(status().isOk());
    }

    public String getResponseString(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userService, bCryptPasswordEncoder, authenticationManager)).build();
        usernamePassword = new HashMap<>();
        usernamePassword.put("username", testUsername);
        usernamePassword.put("password", testPassword);
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        httpRequest("/auth", HttpMethod.GET).andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("isLogin\":false")));
    }

    @Test
    public void testUserIsNotExistWhenLogin() throws Exception {
        Mockito.when(userService.loadUserByUsername(testUsername)).thenThrow(new UsernameNotFoundException("Username Not Found"));
        httpRequest("/auth/login", HttpMethod.POST, usernamePassword).andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("用户不存在")));
    }

    @Test
    void testLoginAndLogout() throws Exception {
        // 未登录时，/auth 接口返回未登录状态
        returnNotLoginByDefault();

        // 使用 /auth/login 登录
        Mockito.when(userService.loadUserByUsername(testUsername))
                .thenReturn(new User(testUsername, bCryptPasswordEncoder.encode(testPassword), Collections.emptyList()));

        MvcResult mvcResult = httpRequest("/auth/login", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("登录成功")))
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession();

        // 检查 /auth 的返回值，处于登录状态
        Mockito.when(userService.getUserByUsername(testUsername)).thenReturn(testUser);
        httpRequest("/auth", HttpMethod.GET, null, session)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains(testUsername)));

        // 使用 /auth/logout 登出
        httpRequest("/auth/logout", HttpMethod.GET)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("注销成功")));
    }

    @Test
    void testRegisterRule() throws Exception {
        usernamePassword.put("username", null);
        usernamePassword.put("password", testPassword);
        httpRequest("/auth/register", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("用户名|密码为空")));

        usernamePassword.put("username", testUsername);
        usernamePassword.put("password", null);
        httpRequest("/auth/register", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("用户名|密码为空")));

        usernamePassword.put("username", "");
        usernamePassword.put("password", testPassword);
        httpRequest("/auth/register", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("用户名长度 1 - 15 个字符")));

        usernamePassword.put("username", "usernameOverflow");
        usernamePassword.put("password", testPassword);
        httpRequest("/auth/register", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("用户名长度 1 - 15 个字符")));

        usernamePassword.put("username", testUsername);
        usernamePassword.put("password", "12345");
        httpRequest("/auth/register", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("密码长度 6 - 16 个字符")));

        usernamePassword.put("username", testUsername);
        usernamePassword.put("password", "passwordOverflowSixteen");
        httpRequest("/auth/register", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("密码长度 6 - 16 个字符")));
    }

    @Test
    public void repeatRegister() throws Exception {
        doThrow(new DuplicateKeyException("Duplicate Key Exception")).when(userService).register(any());

        httpRequest("/auth/register", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("该用户名已经注册")));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        Mockito.when(userService.loadUserByUsername(testUsername)).thenReturn(new User(testUsername, bCryptPasswordEncoder.encode(testPassword), Collections.emptyList()));


        MvcResult mvcResult = httpRequest("/auth/register", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("注册成功")))
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession();
        Mockito.when(userService.getUserByUsername(testUsername)).thenReturn(testUser);
        httpRequest("/auth", HttpMethod.GET, null, session)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains(testUsername)));
    }

    @Test
    void testLogoutWhenNotLogin() throws Exception {
        httpRequest("/auth/logout", HttpMethod.GET).andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("用户尚未登录")));
    }

    @Test
    public void testPasswordIsNotCorrectWhenLogin() throws Exception {
        Mockito.when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad Credentials Exception"));
        Mockito.when(userService.loadUserByUsername(testUsername))
                .thenReturn(new User(testUsername, bCryptPasswordEncoder.encode(testPassword), Collections.emptyList()));

        httpRequest("/auth/login", HttpMethod.POST, usernamePassword)
                .andExpect(result -> Assertions.assertTrue(getResponseString(result).contains("密码不正确")));
    }
}
