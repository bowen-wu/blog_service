package com.blog.service;

import com.blog.dao.UserDao;
import com.blog.entity.AuthResponse;
import com.blog.entity.Response;
import com.blog.entity.ResponseStatus;
import com.blog.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserDao mockUserDao;
    @InjectMocks
    UserService userService;

    @Test
    public void testRegister() {
        User testUser = new User(1, "testUser");
        userService.register(testUser);
        Mockito.verify(mockUserDao).insertUser(testUser);
    }

    @Test
    public void testGetUserByUsername() {
        String testUsername = "testUsername";
        userService.getUserByUsername(testUsername);
        Mockito.verify(mockUserDao).getUserByUsername(testUsername);
    }

    @Test
    public void returnNotLoginResponse() {
        String testUsername = "testUsername";
        Assertions.assertEquals(ResponseStatus.ok, userService.getLoginStatus(testUsername).getStatus());
        Assertions.assertFalse(userService.getLoginStatus(testUsername).isIsLogin());
    }

    @Test
    public void returnAlreadyLoginResponse() {
        String testUsername = "testUsername";
        User testUser = new User(1, testUsername, "myEncodedPassword", "", Instant.now(), Instant.now());
        Mockito.when(mockUserDao.getUserByUsername(testUsername)).thenReturn(testUser);
        AuthResponse loginStatus = userService.getLoginStatus(testUsername);
        Assertions.assertTrue(loginStatus.isIsLogin());
        Assertions.assertEquals(testUser, loginStatus.getData());
    }

    @Test
    public void returnNotLoginWhenLogout() {
        String testUsername = "testUsername";
        Assertions.assertEquals(ResponseStatus.fail, userService.logout(testUsername).getStatus());
        Assertions.assertEquals("用户尚未登录", userService.logout(testUsername).getMsg());
    }

    @Test
    public void logoutWhenAlreadyLogin() {
        String testUsername = "testUsername";
        User testUser = new User(1, testUsername, "myEncodedPassword", "", Instant.now(), Instant.now());
        Mockito.when(mockUserDao.getUserByUsername(testUsername)).thenReturn(testUser);
        Response logout = userService.logout(testUsername);
        Assertions.assertEquals(ResponseStatus.ok, logout.getStatus());
        Assertions.assertEquals("注销成功", logout.getMsg());
    }

    @Test
    public void throwExceptionWhenUserNotFound() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("testUsername"));
    }

    @Test
    public void returnUserDetailsWhenUserFound() {
        String testUsername = "testUsername";
        User testUser = new User(1, testUsername, "myEncodedPassword", "", Instant.now(), Instant.now());
        Mockito.when(mockUserDao.getUserByUsername(testUsername)).thenReturn(testUser);
        UserDetails userDetails = userService.loadUserByUsername(testUsername);
        Assertions.assertEquals(testUsername, userDetails.getUsername());
        Assertions.assertEquals("myEncodedPassword", userDetails.getPassword());
    }

}
