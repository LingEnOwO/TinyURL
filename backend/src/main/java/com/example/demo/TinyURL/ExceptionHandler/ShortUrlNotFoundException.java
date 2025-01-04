package com.example.demo.TinyURL.ExceptionHandler;

public class ShortUrlNotFoundException extends RuntimeException{
    public ShortUrlNotFoundException(String shortUrl){
        super("Short URL not found: " + shortUrl);
    }
}
