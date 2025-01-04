package com.example.demo.TinyURL.ErrorResponse;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String message;
    private final String timestamp;

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
