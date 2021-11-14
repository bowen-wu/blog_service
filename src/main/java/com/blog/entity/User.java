package com.blog.entity;

import sun.jvm.hotspot.oops.Instance;

public class User {
    int id;
    String username;
    String avatar;
    Instance updatedAt;
    Instance createdAt;

    public User(int id, String username, String avatar, Instance updatedAt, Instance createdAt) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }
}
