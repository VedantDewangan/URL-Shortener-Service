package com.example.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "url_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String originalUrl;

    @Column(unique = true)
    private String shortCode;

    private LocalDateTime createdAt;

    private Long clickCount;
}
