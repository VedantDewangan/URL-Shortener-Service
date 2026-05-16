package com.example.urlshortener.controller;

import com.example.urlshortener.dto.ShortenUrlRequest;
import com.example.urlshortener.dto.ShortenUrlResponse;
import com.example.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService){
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody @Valid ShortenUrlRequest shortenUrlRequest){
        String originalUrl = shortenUrlRequest.url();
        String shortCode = urlShortenerService.shortenUrl(originalUrl);

        String shortUrl = "http://localhost:8000/" + shortCode;

        ShortenUrlResponse shortenUrlResponse = new ShortenUrlResponse(shortUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(shortenUrlResponse);
    }
}
