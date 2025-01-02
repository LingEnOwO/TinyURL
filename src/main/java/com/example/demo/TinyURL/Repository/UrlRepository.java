package com.example.demo.TinyURL.Repository;

import com.example.demo.TinyURL.Entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsByShortUrl(String shortUrl);

    Optional<Url> findByShortUrl(String shortUrl);

    Optional<Url> findByLongUrl(String longUrl);
    @Query("SELECT u FROM Url u WHERE u.user.id = :userId ORDER BY u.createdDate")
    List<Url> findByUserIdOrderByCreatedDate(@Param("userId") Long userId);

    Optional<Url> findByShortUrlAndUserId(String shortUrl, Long userId);
}
