package com.jonyshev.intershop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cache")
public record CacheProperties(
        int ttlSeconds,
        String prefix
) {
}