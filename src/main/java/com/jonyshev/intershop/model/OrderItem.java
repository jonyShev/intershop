package com.jonyshev.intershop.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("order_items")
public class OrderItem {

    @Id
    private Long id;

    private Long orderId;
    private Long itemId;
    private String title;
    private String description;
    private String imgPath;
    private BigDecimal price;
    private int count;
}