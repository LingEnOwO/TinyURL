package com.example.demo.TinyURL.ErrorResponse;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private String timestamp;

    public ErrorResponse(String message){
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
