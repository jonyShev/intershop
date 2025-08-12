package com.jonyshev.intershop.dto;

import com.jonyshev.intershop.dto.ItemCacheDto;

import java.util.List;

public record CatalogPageCache(
        List<ItemCacheDto> items
) {}
