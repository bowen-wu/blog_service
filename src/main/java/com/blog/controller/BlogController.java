package com.blog.controller;

import com.blog.entity.AuthResult;
import com.blog.entity.Blog;
import com.blog.entity.BlogResult;
import com.blog.entity.Result;
import com.blog.service.BlogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

@RestController
public class BlogController {
    BlogService blogService;

    @Inject
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
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
        String title = params.get("title");
        String content = params.get("content");

        if (title == null || title.length() >= 100) {
            return Result.failure("博客标题不能为空，且不超过100个字符");
        }
        if (content == null || content.length() >= 10000) {
            return Result.failure("博客内容不能为空，且不超过10000个字符");
        }
        String description = params.get("description") == null ? content.substring(0, 20) : params.get("description");

        Blog blog = blogService.createBlog(title, content, description);
        if (blog == null) {
            return Result.success("登录后才能操作", null);
        }
        return Result.success("创建成功", blog);
    }
}
