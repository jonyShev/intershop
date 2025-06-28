package com.jonyshev.intershop.config;

import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInit {

    @Bean
    CommandLineRunner initItems(ItemRepository itemRepository) {
        return args -> {
            if (itemRepository.count() == 0) {
                itemRepository.saveAll(List.of(
                        Item.builder()
                                .title("Пицца")
                                .description("Вкусная пицца с сыром")
                                .imgPath("/images/pizza.png")
                                .price(new BigDecimal("12.99"))
                                .build(),

                        Item.builder()
                                .title("Бургер")
                                .description("Сочный бургер с говядиной")
                                .imgPath("/images/burger.png")
                                .price(new BigDecimal("8.99"))
                                .build(),

                        Item.builder()
                                .title("Суши")
                                .description("Набор роллов")
                                .imgPath("/images/sushi.png")
                                .price(new BigDecimal("15.99"))
                                .build(),

                        Item.builder()
                                .title("Паста")
                                .description("Паста карбонара")
                                .imgPath("/images/pasta.png")
                                .price(new BigDecimal("10.99"))
                                .build(),

                        Item.builder()
                                .title("Стейк")
                                .description("Говяжий стейк")
                                .imgPath("/images/steak.png")
                                .price(new BigDecimal("18.50"))
                                .build(),

                        Item.builder()
                                .title("Салат")
                                .description("Овощной салат")
                                .imgPath("/images/salad.png")
                                .price(new BigDecimal("6.75"))
                                .build()
                ));
            }
        };
    }
}