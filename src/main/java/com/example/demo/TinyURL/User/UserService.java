package com.example.demo.TinyURL.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public void registerUser(UserRequest userRequest){
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
}
