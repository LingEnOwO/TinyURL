package com.example.demo.TinyURL.Url;

import com.example.demo.TinyURL.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    boolean existsByShortUrl(String shortUrl);

    Optional<UrlMapping> findByShortUrl(String shortUrl);

    Optional<UrlMapping> findByLongUrl(String longUrl);
    @Query("SELECT u FROM UrlMapping u WHERE u.user.id = :userId ORDER BY u.createdDate")
    List<UrlMapping> findByUserIdOrderByCreatedDate(@Param("userId") Long userId);

    Optional<UrlMapping> findByShortUrlAndUserId(String shortUrl, Long userId);
}
