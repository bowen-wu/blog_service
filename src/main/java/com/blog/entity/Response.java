package com.blog.entity;

enum ResponseStatus {
    OK, FAIL
}

public class Response {
    ResponseStatus status;
    String msg;

    public Response(ResponseStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
