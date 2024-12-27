package com.example.demo.TinyURL.Url;

import com.example.demo.TinyURL.ErrorResponse.ErrorResponse;
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
    public ResponseEntity<String> createShortUrl(@RequestBody UrlRequest request, @RequestParam String username){
        try{
            String shortUrl = urlService.generateShortUrl(request, username);
            return ResponseEntity.ok(shortUrl);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<ErrorResponse> redirectToLongUrl(@PathVariable String shortUrl){
        try {
            String longUrl = urlService.getLongUrl(shortUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(longUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Found
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage())); // 400 Bad Request
        }

    }
}
