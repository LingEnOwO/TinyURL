package com.example.demo.TinyURL.Url;

import com.example.demo.TinyURL.User.User;
import com.example.demo.TinyURL.User.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private UserRepository userRepository;

    public String generateShortUrl(UrlRequest request, String username){
        // Fetch the user
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if alias is already in use
        if (request.getAlias() != null && urlRepository.existsByShortUrl(request.getAlias())){
            throw new IllegalArgumentException("Alias already in use");
        }

        // Generate a unique short URL if no alias is provided
        String shortUrl = (request.getAlias() != null) ? request.getAlias() : generateUniqueHash();

        // Save to database
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(request.getLongUrl());
        mapping.setShortUrl(shortUrl);
        mapping.setUser(user); // Associate the user
        mapping.onCreate();
        mapping.setExpirationDate(LocalDateTime.now().plusDays(30));
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

    public String getLongUrl(String shortUrl) {
        // Fetch the UrlMapping for the short URL
        UrlMapping urlMapping = urlRepository.findByShortUrl(shortUrl).orElseThrow(() -> new IllegalArgumentException("Short URL not found"));
        //return urlRepository.findByShortUrl(shortUrl).map(UrlMapping::getLongUrl).orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));

        // Check if the URL has expired
        if (urlMapping.getExpirationDate() != null && urlMapping.getExpirationDate().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Short URL has expired");
        }

        // Update expirationDate to 30 days from now
        urlMapping.setExpirationDate(LocalDateTime.now().plusDays(30));
        urlRepository.save(urlMapping);

        return urlMapping.getLongUrl();
    }
}


