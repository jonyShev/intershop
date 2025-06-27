package com.jonyshev.intershop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String imgPath;

    private Integer count;

    private Integer price;
}