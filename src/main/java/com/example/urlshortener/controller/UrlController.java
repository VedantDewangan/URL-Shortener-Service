package com.example.urlshortener.controller;

import com.example.urlshortener.dto.ShortenUrlRequest;
import com.example.urlshortener.dto.ShortenUrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService){
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/api/v1/url/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody @Valid ShortenUrlRequest shortenUrlRequest){
        String originalUrl = shortenUrlRequest.url();

        String shortCode = urlShortenerService.shortenUrl(originalUrl);

        String shortUrl = "http://localhost:8080/" + shortCode;
        ShortenUrlResponse shortenUrlResponse = new ShortenUrlResponse(shortUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(shortenUrlResponse);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){
        String originalUrl = urlShortenerService.getOriginalUrlAndIncrementClicks(shortCode);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/api/v1/url/stats/{shortCode}")
    public ResponseEntity<UrlStatsResponse> getUrlStats(@PathVariable String shortCode){
        UrlStatsResponse urlStatsResponse = urlShortenerService.getStats(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).body(urlStatsResponse);
    }
}
