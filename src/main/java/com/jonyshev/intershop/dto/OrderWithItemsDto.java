package com.jonyshev.intershop.dto;

import com.jonyshev.intershop.model.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderWithItemsDto {
    private Long id;
    private BigDecimal totalSum;
    private List<OrderItem> orderItems;
}
