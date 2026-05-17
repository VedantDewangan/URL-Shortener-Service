package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.exception.AliasAlreadyExistsException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlShortenerService {
    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final UrlMappingRepository urlMappingRepository;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository){
        this.urlMappingRepository = urlMappingRepository;
    }

    @Transactional
    public String shortenUrl(String originalUrl, String customAlias, Integer hoursToExpire){

        if(customAlias != null && !customAlias.isBlank()){
            Optional<UrlMapping> existing = urlMappingRepository.findByShortCode(customAlias);

            if(existing.isPresent()){
                throw new AliasAlreadyExistsException(
                        "Alias " + customAlias + " is already in use."
                );
            }
        }

        UrlMapping urlMapping = new UrlMapping();

        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setCreatedAt(LocalDateTime.now());

        if(hoursToExpire != null){
            urlMapping.setExpirationDate(
                    LocalDateTime.now().plusHours(hoursToExpire)
            );
        }

        UrlMapping saved = urlMappingRepository.save(urlMapping);

        String shortCode;

        if(customAlias != null && !customAlias.isBlank()){
            shortCode = customAlias;
        } else {
            shortCode = encodeBase62(saved.getId());
        }

        saved.setShortCode(shortCode);

        return shortCode;
    }

    @Transactional
    public String getOriginalUrlAndIncrementClicks(String shortCode){
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode).
                orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));

        if (urlMapping.getExpirationDate() != null && urlMapping.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new UrlNotFoundException("This link has expired and is no longer active.");
        }

        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);

        return urlMapping.getOriginalUrl();
    }

    public String encodeBase62(Long number){
        if(number==0){
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        long num = number;
        StringBuilder sb = new StringBuilder();

        while(num>0){
            int rem = (int) (num % 62);
            sb.append(BASE62_CHARS.charAt(rem));
            num = num/62;
        }

        return sb.reverse().toString();
    }

    public UrlStatsResponse getStats(String shortCode){
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode).
                orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));

        String shortUrl = "http://localhost:8080/" + urlMapping.getShortCode();

        return new UrlStatsResponse(
                urlMapping.getOriginalUrl(),
                shortUrl,
                urlMapping.getCreatedAt(),
                urlMapping.getClickCount()
        );
    }
}
