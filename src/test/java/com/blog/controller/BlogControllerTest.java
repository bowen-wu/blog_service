package com.blog.controller;

import com.blog.entity.Blog;
import com.blog.entity.BlogResult;
import com.blog.entity.Result;
import com.blog.entity.ResultStatus;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class BlogControllerTest {
    private ObjectMapper objectMapper;
    private MockMvc mvc;
    private final Integer testPageNo = 1;
    private final Integer testPageSize = 10;
    private final Integer testUserId = 1;
    private final User testUser = new User(1, "testUsername", "", "", Instant.now(), Instant.now());
    private final Blog testBlog = new Blog(1, 1, testUser, "title", "content", "description", Instant.now(), Instant.now());
    private final List<Blog> testBlogList = new ArrayList<>();
    private final Map<String, String> addBlog = new HashMap<>();

    @Mock
    BlogService blogService;
    @Mock
    UserService userService;

    public String httpRequest(String api, HttpMethod method) throws Exception {
        return httpRequest(api, method, null);
    }

    public String httpRequest(String api, HttpMethod method, Object body) throws Exception {
        return httpRequest(api, method, body, null);
    }

    public String httpRequest(String api, HttpMethod method, Object body, Object session) throws Exception {
        MockHttpServletRequestBuilder request = request(method, api).contentType(MediaType.APPLICATION_JSON);
        if (body != null) {
            request.content(new ObjectMapper().writeValueAsString(body));
        }
        if (session != null) {
            request.session((MockHttpSession) Objects.requireNonNull(session));
        }
        MvcResult result = mvc.perform(request).andExpect(status().isOk()).andReturn();
        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new BlogController(blogService, userService)).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        testBlogList.add(testBlog);
        addBlog.put("title", "title");
        addBlog.put("content", "content");
    }

    @Test
    void getBlogs() throws Exception {
        when(blogService.getBlogs(10, 10, 1)).thenReturn(BlogResult.success("获取成功", testBlogList, 1, 10, 1));
        String responseBodyAsString = httpRequest("/blog?page=10&userId=1", HttpMethod.GET);
        BlogResult blogResult = objectMapper.readValue(responseBodyAsString, BlogResult.class);
        assertEquals(blogResult.getMsg(), "获取成功");
        assertEquals(blogResult.getPage(), 10);
        assertEquals(blogResult.getTotal(), 1);
        assertEquals(blogResult.getTotalPage(), 1);
        // TODO: blogResult.getData() == testBlogList
    }

    @Test
    public void getBlogDetailFail() throws Exception {
        when(blogService.getBlogInfoById(111)).thenReturn(null);
        String responseBodyAsString = httpRequest("/blog/111", HttpMethod.GET);
        Result<Blog> blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });
        assertEquals(blogResult.getMsg(), "系统异常");
        assertNull(blogResult.getData());
    }

    @Test
    public void getBlogDetailSuccess() throws Exception {
        when(blogService.getBlogInfoById(111)).thenReturn(testBlog);
        String responseBodyAsString = httpRequest("/blog/111", HttpMethod.GET);
        Result<Blog> blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });
        assertEquals(blogResult.getMsg(), "获取成功");
        // TODO: blogResult.getData == testBlog
    }

    @Test
    public void testCreateBlogRule() throws Exception {
        when(userService.getLoggedInUser()).thenReturn(null);
        String responseBodyAsString = httpRequest("/blog", HttpMethod.POST, addBlog);
        Result<Blog> blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "登录后才能操作");
        assertEquals(blogResult.getStatus(), ResultStatus.ok);

        when(userService.getLoggedInUser()).thenReturn(testUser);
        addBlog.put("title", "");
        responseBodyAsString = httpRequest("/blog", HttpMethod.POST, addBlog);
        blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "博客标题不能为空，且不超过100个字符");
        assertEquals(blogResult.getStatus(), ResultStatus.fail);


        when(userService.getLoggedInUser()).thenReturn(testUser);
        addBlog.put("title", "title");
        addBlog.put("content", "");
        responseBodyAsString = httpRequest("/blog", HttpMethod.POST, addBlog);
        blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "博客内容不能为空，且不超过10000个字符");
        assertEquals(blogResult.getStatus(), ResultStatus.fail);
    }

    @Test
    public void createBlogSuccess() throws Exception {
        when(userService.getLoggedInUser()).thenReturn(testUser);
        lenient().when(blogService.createBlog(any())).thenReturn(testBlog);
        String responseBodyAsString = httpRequest("/blog", HttpMethod.POST, addBlog);
        Result<Blog> blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "创建成功");
        assertEquals(blogResult.getStatus(), ResultStatus.ok);
        // TODO: blogResult.getData == testBlog
    }

    @Test
    public void testUpdateBlogRule() throws Exception {
        when(userService.getLoggedInUser()).thenReturn(null);
        String responseBodyAsString = httpRequest("/blog/1", HttpMethod.PATCH, addBlog);
        Result<Blog> blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "登录后才能操作");
        assertEquals(blogResult.getStatus(), ResultStatus.ok);

        when(userService.getLoggedInUser()).thenReturn(testUser);
        addBlog.put("title", "");
        responseBodyAsString = httpRequest("/blog/1", HttpMethod.PATCH, addBlog);
        blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "博客标题不能为空，且不超过100个字符");
        assertEquals(blogResult.getStatus(), ResultStatus.fail);


        when(userService.getLoggedInUser()).thenReturn(testUser);
        addBlog.put("title", "title");
        addBlog.put("content", "");
        responseBodyAsString = httpRequest("/blog/11", HttpMethod.PATCH, addBlog);
        blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "博客内容不能为空，且不超过10000个字符");
        assertEquals(blogResult.getStatus(), ResultStatus.fail);
    }

    @Test
    public void testUpdateBlogSuccess() throws Exception {
        when(userService.getLoggedInUser()).thenReturn(testUser);
        when(blogService.updateBlog(any())).thenReturn(Result.success("修改成功", testBlog));
        String responseBodyAsString = httpRequest("/blog/111", HttpMethod.PATCH, addBlog);
        Result<Blog> blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "修改成功");
        assertEquals(blogResult.getData().getTitle(), testBlog.getTitle());
        assertEquals(blogResult.getData().getContent(), testBlog.getContent());
        assertEquals(blogResult.getData().getDescription(), testBlog.getDescription());
        assertEquals(blogResult.getData().getId(), testBlog.getId());
        // TODO: blogResult.getData == testBlog
    }

    @Test
    public void testDeleteBlogFail() throws Exception {
        when(userService.getLoggedInUser()).thenReturn(null);
        String responseBodyAsString = httpRequest("/blog/1", HttpMethod.DELETE);
        Result<Blog> blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "登录后才能操作");
    }

    @Test
    public void deleteBlogSuccess() throws Exception {
        when(userService.getLoggedInUser()).thenReturn(testUser);
        when(blogService.deleteBlog(11, testUser)).thenReturn(Result.success("删除成功", null));
        String responseBodyAsString = httpRequest("/blog/11", HttpMethod.DELETE);
        Result<Blog> blogResult = objectMapper.readValue(responseBodyAsString, new TypeReference<Result<Blog>>() {
        });

        assertEquals(blogResult.getMsg(), "删除成功");
        assertEquals(blogResult.getStatus(), ResultStatus.ok);
        assertNull(blogResult.getData());
    }

}
