package com.example.demo.TinyURL.Service;

import com.example.demo.TinyURL.DTO.LoginRequest;
import com.example.demo.TinyURL.DTO.RegisterRequest;
import com.example.demo.TinyURL.Repository.UrlRepository;
import com.example.demo.TinyURL.DTO.UrlResponse;
import com.example.demo.TinyURL.Entity.User;
import com.example.demo.TinyURL.Repository.UserRepository;
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

    public List<UrlResponse> getUserUrls(String username){
        // Fetch the user by username
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch URLs and map them to UrlResponse DTOs
        return urlRepository.findByUserIdOrderByCreatedDate(user.getId()).stream()
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
