package com.example.demo.TinyURL.Url;

import com.example.demo.TinyURL.ErrorResponse.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/url")
public class UrlController {
    @Autowired
    private UrlService urlService;

    @PostMapping
    public ResponseEntity<Map<String, String >> createShortUrl(@RequestBody UrlRequest request, @RequestParam String username){
        try{
            String shortUrl = urlService.generateShortUrl(request, username);
            Map<String, String> response = new HashMap<>();
            response.put("shortUrl", shortUrl); // Build the response
            return ResponseEntity.ok(response);
        } catch (Exception e){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage()); // Send error message to frontend
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{alias}")
    public ResponseEntity<ErrorResponse> redirectToLongUrl(@PathVariable String alias){
        try {
            String longUrl = urlService.getLongUrl(alias);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(longUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Found
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage())); // 400 Bad Request
        }

    }
}
