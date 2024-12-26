package com.example.demo.TinyURL.Url;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    public String generateShortUrl(UrlRequest request){
        if (request.getAlias() != null && urlRepository.existsByShortUrl(request.getAlias())){
            throw new IllegalArgumentException("Alias already in use");
        }

        // Generate a unique short URL if no alias is provided
        String shortUrl = (request.getAlias() != null) ? request.getAlias() : generateUniqueHash();

        // Save to database
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(request.getLongUrl());
        mapping.setShortUrl(shortUrl);
        mapping.onCreate();
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
        return urlRepository.findByShortUrl(shortUrl).map(UrlMapping::getLongUrl).orElseThrow( () -> new ShortUrlNotFoundException(shortUrl));
    }
}


