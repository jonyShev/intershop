package com.jonyshev.intershop.service;

import com.jonyshev.intershop.config.CacheProperties;
import com.jonyshev.intershop.dto.CatalogPageCache;
import com.jonyshev.intershop.dto.ItemCacheDto;
import com.jonyshev.intershop.util.CacheKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCacheService {

    private final ReactiveRedisTemplate<String, ItemCacheDto> itemCacheTemplate;
    private final ReactiveRedisTemplate<String, CatalogPageCache> catalogPageTemplate;
    private final CacheProperties cacheProps;

    public Mono<ItemCacheDto> getItem(Long id) {
        String key = CacheKeys.item(id, cacheProps.prefix());
        return itemCacheTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> putItem(ItemCacheDto dto) {
        String key = CacheKeys.item(dto.id(), cacheProps.prefix());
        return itemCacheTemplate.opsForValue()
                .set(key, dto, Duration.ofSeconds(cacheProps.ttlSeconds()));
    }

    public Mono<CatalogPageCache> getCatalogPage(String search, String sort, int page, int size) {
        String key = CacheKeys.catalog(search, sort, page, size, cacheProps.prefix());
        return catalogPageTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> putCatalogPage(String search, String sort, int page, int size, List<ItemCacheDto> items) {
        String key = CacheKeys.catalog(search, sort, page, size, cacheProps.prefix());
        var payload = new CatalogPageCache(items);
        return catalogPageTemplate.opsForValue()
                .set(key, payload, Duration.ofSeconds(cacheProps.ttlSeconds()));
    }
}
