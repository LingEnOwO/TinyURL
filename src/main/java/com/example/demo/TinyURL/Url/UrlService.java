package com.example.demo.TinyURL.Url;

import com.example.demo.TinyURL.User.User;
import com.example.demo.TinyURL.User.UserRepository;
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
        if (request.getAlias() == null){
            Optional<UrlMapping> existingMapping = urlRepository.findByLongUrl(request.getLongUrl());
            if (existingMapping.isPresent()){
                return existingMapping.get().getShortUrl();
            }
        }

        // Generate a unique alias if no alias is provided
        String alias = (request.getAlias() != null) ? request.getAlias() : generateUniqueHash();
        String shortUrl = baseUrl + alias;

        // Save to database
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(request.getLongUrl());
        mapping.setShortUrl(shortUrl);
        mapping.setUser(user); // Associate the user
        mapping.onCreate();
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
        UrlMapping urlMapping = urlRepository.findByShortUrl(shortUrl).orElseThrow(() -> new IllegalArgumentException("Short URL not found"));

        //return urlRepository.findByShortUrl(shortUrl).map(UrlMapping::getLongUrl).orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));

        // Check if the URL has expired
        if (urlMapping.getExpirationDate() != null && urlMapping.getExpirationDate().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Short URL has expired");
        }

        // Update expirationDate to 30 days from now
        urlMapping.setExpirationDate(LocalDateTime.now().plusDays(expirationDays));
        urlRepository.save(urlMapping);

        return urlMapping.getLongUrl();
    }

    public void renameShortUrl(String username, String oldAlias, String newAlias) {
        // Fetch user by username
        User user = userRepository.findByUsername(username).orElseThrow( () -> new IllegalArgumentException("User not found"));

        // Fetch the URL by old alias and user ID
        String shortUrl = baseUrl + oldAlias;
        UrlMapping urlMapping = urlRepository.findByShortUrlAndUserId(shortUrl, user.getId()).orElseThrow( () -> new IllegalArgumentException("Short URL not found"));

        // Check if the new alias is already use
        String newShortUrl = baseUrl + newAlias;
        if (urlRepository.existsByShortUrl(newShortUrl)) {
            throw new IllegalArgumentException("Alias already in use");
        }

        // Update the shortURL
        urlMapping.setShortUrl(newShortUrl);
        urlRepository.save(urlMapping);
    }

    public void updateLongUrl(String username, String alias, String newLongUrl) {
        // Fetch the user by username
        User user = userRepository.findByUsername(username).orElseThrow( () -> new IllegalArgumentException("User not found"));
        // Fetch the URL by alias and User ID
        UrlMapping urlMapping = urlRepository.findByShortUrlAndUserId(baseUrl + alias, user.getId()).orElseThrow( () -> new IllegalArgumentException("Short URL not found"));

        // Update the long URL
        urlMapping.setLongUrl(newLongUrl);
        urlRepository.save(urlMapping);
    }

    public void deleteShortUrl(String username, String alias) {
        // Fetch the user by username
        User user = userRepository.findByUsername(username).orElseThrow( () -> new IllegalArgumentException("User not found"));
        // Fetch the URL by alias and user
        UrlMapping urlMapping = urlRepository.findByShortUrlAndUserId(baseUrl+alias, user.getId()).orElseThrow( () -> new IllegalArgumentException("Short URL not found"));
        // Delete the URL mapping
        urlRepository.delete(urlMapping);
    }
}


