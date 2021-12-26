package com.blog.controller;

import com.blog.entity.Blog;
import com.blog.entity.BlogResult;
import com.blog.entity.Result;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.UserService;
import com.blog.util.CustomUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;


@RestController
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class BlogController {
    BlogService blogService;
    UserService userService;

    @Inject
    public BlogController(BlogService blogService, UserService userService) {
        this.blogService = blogService;
        this.userService = userService;
    }

    @GetMapping("/blog")
    @ResponseBody
    public BlogResult getBlogs(@RequestParam("page") Integer page, @RequestParam(value = "userId", required = false) Integer userId) {
        int pageSize = 10;
        return blogService.getBlogs(page, pageSize, userId);
    }

    @GetMapping("/blog/{blogId}")
    @ResponseBody
    public Result<Blog> getBlogInfoById(@PathVariable Integer blogId) {
        Blog blogInfoById = blogService.getBlogInfoById(blogId);
        if (blogInfoById == null) {
            return Result.success("系统异常", null);
        }
        return Result.success("获取成功", blogInfoById);
    }

    @PostMapping("/blog")
    @ResponseBody
    public Result<Blog> createBlog(@RequestBody Map<String, String> params) {
        User user = userService.getLoggedInUser();
        if (user == null) {
            return Result.success("登录后才能操作", null);
        }

        String title = params.get("title");
        String content = params.get("content");

        if (!StringUtils.hasText(title) || title.length() >= 100) {
            return Result.failure("博客标题不能为空，且不超过100个字符");
        }
        if (!StringUtils.hasText(content) || content.length() >= 10000) {
            return Result.failure("博客内容不能为空，且不超过10000个字符");
        }

        String description = CustomUtil.getDescription(params.get("description"), content);

        Blog blog = blogService.createBlog(new Blog(null, user.getId(), user, title, content, description, Instant.now(), Instant.now()));
        return Result.success("创建成功", blog);
    }


    @PatchMapping("/blog/{blogId}")
    @ResponseBody
    public Result<Blog> updateBlog(@PathVariable Integer blogId, @RequestBody Map<String, String> params) {
        User user = userService.getLoggedInUser();
        if (user == null) {
            return Result.success("登录后才能操作", null);
        }

        String title = params.get("title");
        String content = params.get("content");
        String description = CustomUtil.getDescription(params.get("description"), content);

        if (!StringUtils.hasText(title) || title.length() >= 100) {
            return Result.failure("博客标题不能为空，且不超过100个字符");
        }
        if (!StringUtils.hasText(content) || content.length() >= 10000) {
            return Result.failure("博客内容不能为空，且不超过10000个字符");
        }

        return blogService.updateBlog(new Blog(blogId, user.getId(), user, title, content, description, null, Instant.now()));
    }

    @DeleteMapping("/blog/{blogId}")
    @ResponseBody
    public Result<Blog> deleteBlog(@PathVariable Integer blogId) {
        User user = userService.getLoggedInUser();
        if (user == null) {
            return Result.success("登录后才能操作", null);
        }
        return blogService.deleteBlog(blogId, user);
    }
}
