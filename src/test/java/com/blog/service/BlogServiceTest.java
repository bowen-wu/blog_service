package com.blog.service;

import com.blog.dao.BlogDao;
import com.blog.entity.Blog;
import com.blog.entity.BlogResult;
import com.blog.entity.ResultStatus;
import com.blog.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {
    @Mock
    BlogDao mockBlogDao;
    @InjectMocks
    BlogService blogService;

    @Test
    public void getBlogsFromDb() {
        blogService.getBlogs(1, 10, null);
        Mockito.verify(mockBlogDao).getBlogs(1, 10, null);
        Mockito.verify(mockBlogDao).getBlogCount(null);
    }

    @Test
    public void returnFailureWhenExceptionThrown() {
        Mockito.when(mockBlogDao.getBlogs(anyInt(), anyInt(), anyInt())).thenThrow(new RuntimeException());

        BlogResult result = blogService.getBlogs(1, 10, null);

        Assertions.assertEquals(ResultStatus.fail, result.getStatus());
        Assertions.assertEquals("系统异常", result.getMsg());
    }

    @Test
    public void returnSuccessResult() {
        int testPage = 1;
        int testPageSize = 10;
        int testUserId = 1;
        User testUser = new User(1, "testUser", "testPassword", "", now(), now());
        List<Blog> mockBlogs = new ArrayList<>();
        mockBlogs.add(new Blog(1, 1, testUser, "title1", "content1", "description1", now(), now()));
        mockBlogs.add(new Blog(2, 1, testUser, "title2", "content2", "description2", now(), now()));
        Mockito.when(mockBlogDao.getBlogs(testPage, testPageSize, testUserId)).thenReturn(mockBlogs);
        Mockito.when(mockBlogDao.getBlogCount(testUserId)).thenReturn(21);

        BlogResult blogs = blogService.getBlogs(testPage, testPageSize, testUserId);

        Assertions.assertEquals(ResultStatus.ok, blogs.getStatus());
        Assertions.assertEquals("获取成功", blogs.getMsg());
        Assertions.assertEquals(mockBlogs, blogs.getData());
        Assertions.assertEquals(1, blogs.getPage());
        Assertions.assertEquals(21, blogs.getTotal());
        Assertions.assertEquals(3, blogs.getTotalPage());
    }
}
