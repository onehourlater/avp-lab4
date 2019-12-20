package com.sample.controller;

public class BaseResponse {

    private final String status;
    private final String body;

    public BaseResponse(String status, String body) {
        this.status = status;
        this.body = body;
    }

    public String getStatus() {
        return status;
    }
    public String getBody() {
        return body;
    }

}