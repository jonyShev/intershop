package com.jonyshev.intershop.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Item {

    @Id
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private BigDecimal price;
    private Integer count = 0;
}