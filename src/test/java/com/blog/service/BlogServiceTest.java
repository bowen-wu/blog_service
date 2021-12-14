package com.blog.service;

import com.blog.dao.BlogDao;
import com.blog.dao.UserDao;
import com.blog.entity.Blog;
import com.blog.entity.BlogResult;
import com.blog.entity.Result;
import com.blog.entity.ResultStatus;
import com.blog.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {
    @Mock
    UserDao mockUserDao;
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

    @Test
    public void getBlogInfoByIdFromDB() {
        User testUser = new User(1, "testUser", "", "", Instant.now(), Instant.now());
        Blog mockBlog = new Blog();
        mockBlog.setUserId(123);
        mockBlog.setUser(testUser);
        Mockito.when(mockBlogDao.getBlogInfoById(anyInt())).thenReturn(mockBlog);
        Mockito.when(mockUserDao.getUserById(123)).thenReturn(testUser);

        Blog blog = blogService.getBlogInfoById(1);
        Mockito.verify(mockBlogDao).getBlogInfoById(1);
        Mockito.verify(mockUserDao).getUserById(123);
        Assertions.assertEquals(blog, mockBlog);
    }

    @Test
    public void returnNullWhenBlogIdIsNotPresent() {
        Mockito.when(mockBlogDao.getBlogInfoById(anyInt())).thenReturn(null);
        Blog blog = blogService.getBlogInfoById(1);
        Assertions.assertNull(blog);
    }

    @Test
    public void testCreateBlog() {
        BlogService spyBlogService = Mockito.spy(blogService);
        String testUsername = "testUsername";
        User testUser = new User(1, testUsername, "myEncodedPassword", "", Instant.now(), Instant.now());
        Mockito.when(mockBlogDao.createBlog(any())).thenReturn(123);
        Blog testBlog = new Blog(1, 1, testUser, "title1", "content1", "description1", now(), now());

        spyBlogService.createBlog(testBlog);

        Mockito.verify(mockBlogDao).createBlog(testBlog);
        Mockito.verify(spyBlogService).getBlogInfoById(123);
    }

    @Test
    public void blogIsNotPresentWhenUpdateBlog() {
        String testUsername = "testUsername";
        User testUser = new User(1, testUsername, "myEncodedPassword", "", Instant.now(), Instant.now());
        Blog testBlog = new Blog(1, 1, testUser, "title1", "content1", "description1", now(), now());
        BlogService spyBlogService = Mockito.spy(blogService);
        Mockito.when(spyBlogService.getBlogInfoById(testBlog.getId())).thenReturn(null);

        Result<Blog> blogResult = spyBlogService.updateBlog(testBlog);

        Assertions.assertEquals(blogResult.getStatus(), ResultStatus.fail);
        Assertions.assertTrue(blogResult.getMsg().contains("博客不存在"));
    }

    @Test
    public void permissionDeniedUpdateBlog() {
        String testUsername = "testUsername";
        User testUser = new User(1, testUsername, "myEncodedPassword", "", Instant.now(), Instant.now());
        BlogService spyBlogService = Mockito.spy(blogService);
        Blog testBlog = new Blog(1, 1, testUser, "title1", "content1", "description1", now(), now());
        Mockito.when(spyBlogService.getBlogInfoById(testBlog.getId())).thenReturn(testBlog);

        Result<Blog> blogResult = spyBlogService.updateBlog(new Blog(1, 11, testUser, "title1", "content1", "description1", now(), now()));

        Assertions.assertEquals(blogResult.getStatus(), ResultStatus.fail);
        Assertions.assertTrue(blogResult.getMsg().contains("无法修改别人的博客"));
    }

    @Test
    public void updateBlog() {
        String testUsername = "testUsername";
        User testUser = new User(1, testUsername, "myEncodedPassword", "", Instant.now(), Instant.now());
        BlogService spyBlogService = Mockito.spy(blogService);
        Blog testBlog = new Blog(111, 1, testUser, "title1", "content1", "description1", now(), now());
        Mockito.when(spyBlogService.getBlogInfoById(testBlog.getId())).thenReturn(testBlog);

        Result<Blog> blogResult = blogService.updateBlog(testBlog);

        Mockito.verify(mockBlogDao).updateBlog(testBlog);
        Mockito.verify(spyBlogService).getBlogInfoById(testBlog.getId());

        Assertions.assertEquals(blogResult.getStatus(), ResultStatus.ok);
        Assertions.assertTrue(blogResult.getMsg().contains("修改成功"));
    }

    @Test
    public void blogIsNotPresentWhenDeleteBlog() {
        Integer deleteBlogId = 1;
        User testUser = new User(1, "testUsername", "myEncodedPassword", "", Instant.now(), Instant.now());
        BlogService spyBlogService = Mockito.spy(blogService);
        Mockito.lenient().when(spyBlogService.getBlogInfoById(deleteBlogId)).thenReturn(null);

        Result<Blog> blogResult = spyBlogService.deleteBlog(deleteBlogId, testUser);

        Assertions.assertEquals(blogResult.getStatus(), ResultStatus.fail);
        Assertions.assertTrue(blogResult.getMsg().contains("博客不存在"));
    }

    @Test
    public void permissionDeniedDeleteBlog() {
        Integer deleteBlogId = 1;
        User testUser = new User(111, "testUsername", "myEncodedPassword", "", Instant.now(), Instant.now());
        BlogService spyBlogService = Mockito.spy(blogService);

        User blogUser = new User(1, "testUsername", "myEncodedPassword", "", Instant.now(), Instant.now());
        Blog testBlog = new Blog(1, 1, blogUser, "title1", "content1", "description1", now(), now());

        Mockito.when(spyBlogService.getBlogInfoById(deleteBlogId)).thenReturn(testBlog);

        Result<Blog> blogResult = spyBlogService.deleteBlog(deleteBlogId, testUser);

        Assertions.assertEquals(blogResult.getStatus(), ResultStatus.fail);
        Assertions.assertTrue(blogResult.getMsg().contains("无法删除别人的博客"));
    }

    @Test
    public void deleteBlog() {
        String testUsername = "testUsername";
        User testUser = new User(1, testUsername, "myEncodedPassword", "", Instant.now(), Instant.now());
        BlogService spyBlogService = Mockito.spy(blogService);
        Blog testBlog = new Blog(111, 1, testUser, "title1", "content1", "description1", now(), now());
        Mockito.when(spyBlogService.getBlogInfoById(testBlog.getId())).thenReturn(testBlog);

        Result<Blog> blogResult = blogService.deleteBlog(testBlog.getId(), testUser);

        Mockito.verify(spyBlogService).getBlogInfoById(testBlog.getId());
        Mockito.verify(mockBlogDao).deleteBlog(testBlog.getId());

        Assertions.assertEquals(blogResult.getStatus(), ResultStatus.ok);
        Assertions.assertTrue(blogResult.getMsg().contains("删除成功"));

    }
}
