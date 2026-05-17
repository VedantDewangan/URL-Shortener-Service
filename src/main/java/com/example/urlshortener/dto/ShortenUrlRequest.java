package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(
        @NotEmpty(message = "URL cannot be empty")
        @URL(message = "A valid URL is required")
        String url,
        String customAlias
) {
}
