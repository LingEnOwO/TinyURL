package com.example.demo.TinyURL.Url;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    boolean existsByShortUrl(String shortUrl);

    Optional<UrlMapping> findByShortUrl(String shortUrl);
}
