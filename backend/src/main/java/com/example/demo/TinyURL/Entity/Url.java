package com.example.demo.TinyURL.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "long_url", nullable = false, columnDefinition = "TEXT")
    private String longUrl;
    @Column(name = "short_url", nullable = false, unique = true)
    private String shortUrl;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    @Column(name = "created_date", nullable = false, unique = true, updatable = false)
    private LocalDateTime createdDate;
    @Column(name = "expiration_date", nullable = true)
    private LocalDateTime expirationDate;

    @Column(name = "click_count")
    private Integer clickCount = 0;// Initialize to 0
    @Column(name = "last_time_click")
    private LocalDateTime lastClicked;

    @PrePersist
    protected void onCreate(){
        this.createdDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLastClicked() {return lastClicked;}

    public void setLastClicked(LocalDateTime lastClicked) {this.lastClicked = lastClicked;}

    public Integer getClickCount() {return clickCount;}

    public void setClickCount(Integer clickCount) {this.clickCount = clickCount;}
}
