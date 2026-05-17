package com.example.urlshortener.service;

import com.example.urlshortener.repository.UrlMappingRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CleanupService {

    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);

    private final UrlMappingRepository urlMappingRepository;

    public CleanupService(UrlMappingRepository urlMappingRepository){
        this.urlMappingRepository = urlMappingRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredUrls() {
        logger.info("Running cleanup job...");

        long deletedCount =
                urlMappingRepository.deleteByExpirationDateBefore(
                        LocalDateTime.now()
                );

        logger.info("Deleted {} expired URLs", deletedCount);
    }
}
