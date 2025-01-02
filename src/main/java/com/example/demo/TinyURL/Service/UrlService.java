package com.example.demo.TinyURL.Service;

import com.example.demo.TinyURL.DTO.UrlRequest;
import com.example.demo.TinyURL.Entity.Url;
import com.example.demo.TinyURL.Repository.UrlRepository;
import com.example.demo.TinyURL.Entity.User;
import com.example.demo.TinyURL.Repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${short.url.base}") // Fetch the base URL from properties
    private String baseUrl;

    @Value("${short.url.expirationDays}")
    private long expirationDays;
    public String generateShortUrl(UrlRequest request, String username){
        // Fetch the user
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if alias is already in use
        if (request.getAlias() != null && urlRepository.existsByShortUrl(request.getAlias())){
            throw new IllegalArgumentException("Alias already in use");
        }

        // Check if the long URL already exists and no alias is provided
        if (request.getLongUrl() != null){
            Optional<Url> existingMapping = urlRepository.findByLongUrl(request.getLongUrl());
            if (existingMapping.isPresent()){
                return existingMapping.get().getShortUrl();
            }
        }

        // Generate a unique alias if no alias is provided
        String alias = (request.getAlias() != null) ? request.getAlias() : generateUniqueHash();
        String shortUrl = baseUrl + alias;

        // Save to database
        Url mapping = new Url();
        mapping.setLongUrl(request.getLongUrl());
        mapping.setShortUrl(shortUrl);
        mapping.setUser(user); // Associate the user
        //mapping.onCreate();
        mapping.setExpirationDate(LocalDateTime.now().plusDays(expirationDays));
        urlRepository.save(mapping);

        return shortUrl;
    }

    private String generateUniqueHash(){
        String hash;
        do{
            hash = RandomStringUtils.randomAlphanumeric(8);
        } while (urlRepository.existsByShortUrl(hash));
        return hash;
    }

    public String getLongUrl(String alias) {
        // Extract alias from the full short URL
        String shortUrl = baseUrl + alias;
        // Fetch the UrlMapping for the short URL
        Url url = urlRepository.findByShortUrl(shortUrl).orElseThrow(() -> new IllegalArgumentException("Short URL not found"));

        //return urlRepository.findByShortUrl(shortUrl).map(UrlMapping::getLongUrl).orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));

        // Check if the URL has expired
        if (url.getExpirationDate() != null && url.getExpirationDate().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Short URL has expired");
        }

        // Update expirationDate to 30 days from now
        url.setClickCount(url.getClickCount() + 1);
        url.setLastClicked(LocalDateTime.now());
        url.setExpirationDate(LocalDateTime.now().plusDays(expirationDays));
        urlRepository.save(url);

        return url.getLongUrl();
    }

    public void renameShortUrl(String username, String oldAlias, String newAlias) {
        // Fetch user by username
        User user = userRepository.findByUsername(username).orElseThrow( () -> new IllegalArgumentException("User not found"));

        // Fetch the URL by old alias and user ID
        String shortUrl = baseUrl + oldAlias;
        Url url = urlRepository.findByShortUrlAndUserId(shortUrl, user.getId()).orElseThrow( () -> new IllegalArgumentException("Short URL not found"));

        // Check if the new alias is already use
        String newShortUrl = baseUrl + newAlias;
        if (urlRepository.existsByShortUrl(newShortUrl)) {
            throw new IllegalArgumentException("Alias already in use");
        }

        // Update the shortURL
        url.setShortUrl(newShortUrl);
        urlRepository.save(url);
    }

    public void updateLongUrl(String username, String alias, String newLongUrl) {
        // Fetch the user by username
        User user = userRepository.findByUsername(username).orElseThrow( () -> new IllegalArgumentException("User not found"));
        // Fetch the URL by alias and User ID
        Url url = urlRepository.findByShortUrlAndUserId(baseUrl + alias, user.getId()).orElseThrow( () -> new IllegalArgumentException("Short URL not found"));

        // Update the long URL
        url.setLongUrl(newLongUrl);
        urlRepository.save(url);
    }

    public void deleteShortUrl(String username, String alias) {
        // Fetch the user by username
        User user = userRepository.findByUsername(username).orElseThrow( () -> new IllegalArgumentException("User not found"));
        // Fetch the URL by alias and user
        Url url = urlRepository.findByShortUrlAndUserId(baseUrl + alias, user.getId()).orElseThrow( () -> new IllegalArgumentException("Short URL not found"));
        // Delete the URL mapping
        urlRepository.delete(url);
    }

    public int getClickCount(String username, String alias) {
        User user = userRepository.findByUsername(username).orElseThrow( () -> new IllegalArgumentException("User not found"));
        Url url = urlRepository.findByShortUrlAndUserId(baseUrl + alias, user.getId()).orElseThrow( () -> new IllegalArgumentException("Short URL not found"));
        return url.getClickCount();
    }

    public LocalDateTime getLastClicked(String username, String alias) {
        User user = userRepository.findByUsername(username).orElseThrow( () -> new IllegalArgumentException("User not found"));
        Url url = urlRepository.findByShortUrlAndUserId(baseUrl + alias, user.getId()).orElseThrow( () -> new IllegalArgumentException("Short URL not found"));
        return url.getLastClicked();
    }
}


