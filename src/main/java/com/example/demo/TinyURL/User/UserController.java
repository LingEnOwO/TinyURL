package com.example.demo.TinyURL.User;

import com.example.demo.TinyURL.SuccessResponse.SuccessResponse;
import com.example.demo.TinyURL.Url.UrlResponse;
import com.example.demo.TinyURL.Url.UrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.TinyURL.ErrorResponse.ErrorResponse;
import org.springframework.web.bind.annotation.*;

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
            List<UrlResponse> urls = userService.getUserUrls(username);
            return ResponseEntity.ok(urls);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{username}/urls/{oldAlias}")
    public ResponseEntity<?> renameShortUrl(@PathVariable String username, @PathVariable String oldAlias, @RequestParam String newAlias){
        try{
            urlService.renameShortUrl(username, oldAlias, newAlias);
            return ResponseEntity.ok(new SuccessResponse("Short URL renamed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

}
