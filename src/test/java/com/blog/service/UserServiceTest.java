package com.blog.service;

import com.blog.dao.UserDao;
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
        User testUser = new User(1, "testUser", "", "", Instant.now(), Instant.now());
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

    @Test
    public void returnNullWhenNotLoggedIn() {
        User loggedInUser = userService.getLoggedInUser();
        Assertions.assertNull(loggedInUser);
    }

    @Test
    public void returnLoggedInUser() {
        // TODO: mock SecurityContextHolder.getContext().getAuthentication() return not null
    }

}
