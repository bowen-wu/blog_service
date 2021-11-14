package com.blog.entity;

import sun.jvm.hotspot.oops.Instance;

import java.time.Instant;

public class User {
    Integer id;
    String username;
    String avatar;
    Instant updatedAt;
    Instant createdAt;

    public User(Integer id, String username, String avatar, Instant updatedAt, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
