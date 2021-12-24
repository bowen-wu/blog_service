package com.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.Instant;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class Blog {
    private Integer id;
    @JsonIgnore
    private Integer userId;
    private User user;
    private String title;
    private String content;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public Blog() {
    }

    public Blog(Integer id, Integer userId, User user, String title, String content, String description, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.user = user;
        this.title = title;
        this.content = content;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
