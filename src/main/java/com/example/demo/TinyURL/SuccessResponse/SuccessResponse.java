package com.example.demo.TinyURL.SuccessResponse;

import java.time.LocalDateTime;

public class SuccessResponse {
    private final String message;
    private final String timestamp;

    public SuccessResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
