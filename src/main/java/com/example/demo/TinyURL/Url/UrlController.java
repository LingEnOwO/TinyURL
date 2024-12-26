package com.example.demo.TinyURL.Url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(path = "/api/url")
public class UrlController {
    @Autowired
    private UrlService urlService;

    @PostMapping
    public ResponseEntity<String> createShortUrl(@RequestBody UrlRequest request){
        try{
            String shortUrl = urlService.generateShortUrl(request);
            return ResponseEntity.ok(shortUrl);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> redirectToLongUrl(@PathVariable String shortUrl){
        String longUrl = urlService.getLongUrl(shortUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(longUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 found
    }
}
