package com.example.demo.TinyURL.User;

import com.example.demo.TinyURL.Url.UrlRepository;
import com.example.demo.TinyURL.Url.UrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UrlRepository urlRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public void registerUser(RegisterRequest userRequest){
        // Validate username uniqueness
        if (userRepository.existsByUsername(userRequest.getUsername())){
            throw new IllegalArgumentException("Username already exists");
        }
        // Validate email uniqueness
        if (userRepository.existsByEmail(userRequest.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());

        // Create a user entity and save to db
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(hashedPassword);

        userRepository.save(user);
    }

    public boolean authenticateUser(LoginRequest loginRequest){
        // Find user by username
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("Username not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }
        return true;
    }

    public List<UrlResponse> getUserUrls(String username){
        // Fetch the user by username
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch URLs and map them to UrlResponse DTOs
        return urlRepository.findByUserId(user.getId()).stream()
                .map(url ->{
                    UrlResponse response = new UrlResponse();
                    response.setLongUrl(url.getLongUrl());
                    response.setShortUrl(url.getShortUrl());
                    response.setCreatedDate(url.getCreatedDate().toString());
                    response.setExpirationDate(url.getExpirationDate().toString());
                    return response;
                }).collect(Collectors.toList());
    }
}
