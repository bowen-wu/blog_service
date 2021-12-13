package com.blog.service;

import com.blog.dao.BlogDao;
import com.blog.dao.UserDao;
import com.blog.entity.Blog;
import com.blog.entity.BlogResult;
import com.blog.entity.ResultStatus;
import com.blog.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

@Service
public class BlogService {
    BlogDao blogDao;
    UserDao userDao;
    UserService userService;

    @Inject
    public BlogService(BlogDao blogDao, UserDao userDao, UserService userService) {
        this.blogDao = blogDao;
        this.userDao = userDao;
        this.userService = userService;
    }

    public BlogResult getBlogs(Integer page, Integer pageSize, Integer userId) {
        try {
            List<Blog> blogs = blogDao.getBlogs(page, pageSize, userId);
            int total = blogDao.getBlogCount(userId);
            int totalPage = (int) Math.ceil(total * 1.0 / pageSize);
            return BlogResult.success("获取成功", blogs, total, page, totalPage);
        } catch (Exception e) {
            return BlogResult.failure("系统异常");
        }
    }

    public Blog getBlogInfoById(Integer blogId) {
        Blog blog = blogDao.getBlogInfoById(blogId);
        if (blog == null) {
            return null;
        }
        User user = userDao.getUserById(blog.getUserId());
        blog.setUser(user);
        return blog;
    }

    public Blog createBlog(String title, String content, String description) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication == null ? null : authentication.getName());
        if (user == null) {
            return null;
        }
        Integer blogId = blogDao.createBlog(new Blog(null, user.getId(), user, title, content, description, Instant.now(), Instant.now()));
        return getBlogInfoById(blogId);
    }
}
