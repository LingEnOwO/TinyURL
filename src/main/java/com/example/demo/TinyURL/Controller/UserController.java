package com.example.demo.TinyURL.Controller;

import com.example.demo.TinyURL.DTO.LoginRequest;
import com.example.demo.TinyURL.DTO.RegisterRequest;
import com.example.demo.TinyURL.DTO.UpdateLongUrlRequest;
import com.example.demo.TinyURL.SuccessResponse.SuccessResponse;
import com.example.demo.TinyURL.DTO.UrlResponse;
import com.example.demo.TinyURL.Service.UrlService;
import com.example.demo.TinyURL.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.TinyURL.ErrorResponse.ErrorResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UrlService urlService;

    @GetMapping("/urls") // Get user's URLs
    public ResponseEntity<?> getUserUrls(){
        try {
            // Extract authenticated username from the token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            // Fetch URLs for the authenticated user
            List<UrlResponse> urls = userService.getUserUrls(username);
            return ResponseEntity.ok(urls);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/urls/{oldAlias}") // Rename shortURL
    public ResponseEntity<?> renameShortUrl(@PathVariable String oldAlias, @RequestParam String newAlias){
        try{
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            // Rename the short URL
            urlService.renameShortUrl(username, oldAlias, newAlias);
            return ResponseEntity.ok(new SuccessResponse("Short URL renamed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/urls/{alias}/update") // Direct the short URL to a new long URL
    public ResponseEntity<?> updateLongUrl(@PathVariable String alias, @RequestBody UpdateLongUrlRequest request){
        try {
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            urlService.updateLongUrl(username, alias, request.getNewLongUrl());
            return ResponseEntity.ok(new SuccessResponse("Long URL updated successfully"));
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/urls/{alias}") // Delete a short URL
    public ResponseEntity<?> deleteShortUrl(@PathVariable String alias) {
        try {
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            urlService.deleteShortUrl(username, alias);
            return ResponseEntity.ok(new SuccessResponse("Short URL deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/urls/{alias}/clicks") // Get the total count of the short URL got clicked
    public ResponseEntity<?> getClickCount(@PathVariable String alias) {
        try {
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            int clickCount = urlService.getClickCount(username, alias);
            return ResponseEntity.ok(new SuccessResponse("Click count: " + clickCount));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/urls/{alias}/last-clicked") // Get the last-clicked time of the short URL
    public ResponseEntity<?> getLastClicked(@PathVariable String alias) {
        try {
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            LocalDateTime lastClicked = urlService.getLastClicked(username, alias);
            return ResponseEntity.ok(new SuccessResponse("Last clicked time: " + lastClicked));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }
}
