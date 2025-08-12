package com.jonyshev.intershop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonyshev.intershop.dto.CatalogPageCache;
import com.jonyshev.intershop.dto.ItemCacheDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, ItemCacheDto> itemCacheTemplate(
            ReactiveRedisConnectionFactory cf,
            ObjectMapper om
    ) {
        var key = new StringRedisSerializer();
        var value = new Jackson2JsonRedisSerializer<>(om, ItemCacheDto.class);
        var ctx = RedisSerializationContext.<String, ItemCacheDto>newSerializationContext(key)
                .value(value)
                .build();
        return new ReactiveRedisTemplate<>(cf, ctx);
    }

    @Bean
    public ReactiveRedisTemplate<String, CatalogPageCache> catalogPageTemplate(
            ReactiveRedisConnectionFactory cf,
            ObjectMapper om
    ) {
        var key = new StringRedisSerializer();
        var value = new Jackson2JsonRedisSerializer<>(om, CatalogPageCache.class);
        var ctx = RedisSerializationContext.<String, CatalogPageCache>newSerializationContext(key)
                .value(value)
                .build();
        return new ReactiveRedisTemplate<>(cf, ctx);
    }
}
