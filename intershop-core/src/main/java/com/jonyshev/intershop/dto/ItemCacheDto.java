package com.jonyshev.intershop.dto;

import java.math.BigDecimal;

public record ItemCacheDto(
        Long id,
        String title,
        String description,
        String imgPath,
        BigDecimal price
) {}
