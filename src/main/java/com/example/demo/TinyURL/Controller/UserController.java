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

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser (@Valid @RequestBody RegisterRequest registerRequest){
        try{
            userService.registerUser(registerRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            // For frontend to fetch username
            response.put("username", registerRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        try{
            userService.authenticateUser(loginRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            // For frontend to fetch username
            response.put("username", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/{username}/urls") // Get user's URLs
    public ResponseEntity<?> getUserUrls(@PathVariable String username){
        try {
            // Extract authenticated username from the token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();

            // Validate the requested username matches the token username
            if (!authenticatedUsername.equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied: You cannot view another user's URLs"));
            }
            // Fetch URLs for the authenticated user
            List<UrlResponse> urls = userService.getUserUrls(username);
            return ResponseEntity.ok(urls);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{username}/urls/{oldAlias}") // Rename shortURL
    public ResponseEntity<?> renameShortUrl(@PathVariable String username, @PathVariable String oldAlias, @RequestParam String newAlias){
        try{
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();
            // Validate the authenticated username matches the path parameter
            if (!authenticatedUsername.equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied: You can only modify your own URLs"));
            }
            // Rename the short URL
            urlService.renameShortUrl(username, oldAlias, newAlias);
            return ResponseEntity.ok(new SuccessResponse("Short URL renamed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{username}/urls/{alias}/update") // Direct the short URL to a new long URL
    public ResponseEntity<?> updateLongUrl(@PathVariable String username, @PathVariable String alias, @RequestBody UpdateLongUrlRequest request){
        try {
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();
            // Validate the authenticated username matches the path parameter
            if (!authenticatedUsername.equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied: You can only modify your own URLs"));
            }
            urlService.updateLongUrl(username, alias, request.getNewLongUrl());
            return ResponseEntity.ok(new SuccessResponse("Long URL updated successfully"));
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{username}/urls/{alias}") // Delete a short URL
    public ResponseEntity<?> deleteShortUrl(@PathVariable String username, @PathVariable String alias) {
        try {
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();
            // Validate the authenticated username matches the path parameter
            if (!authenticatedUsername.equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied: You can only delete your own URLs"));
            }
            urlService.deleteShortUrl(username, alias);
            return ResponseEntity.ok(new SuccessResponse("Short URL deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{username}/urls/{alias}/clicks") // Get the total count of the short URL got clicked
    public ResponseEntity<?> getClickCount(@PathVariable String username, @PathVariable String alias) {
        try {
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();
            // Validate the authenticated username matches the path parameter
            if (!authenticatedUsername.equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied: You can only view your own info"));
            }
            int clickCount = urlService.getClickCount(username, alias);
            return ResponseEntity.ok(new SuccessResponse("Click count: " + clickCount));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{username}/urls/{alias}/last-clicked") // Get the last-clicked time of the short URL
    public ResponseEntity<?> getLastClicked(@PathVariable String username, @PathVariable String alias) {
        try {
            // Extract the authenticated username from the JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();
            // Validate the authenticated username matches the path parameter
            if (!authenticatedUsername.equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied: You can only view your own info"));
            }
            LocalDateTime lastClicked = urlService.getLastClicked(username, alias);
            return ResponseEntity.ok(new SuccessResponse("Last clicked time: " + lastClicked));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }
}
