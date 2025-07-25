package com.jonyshev.intershop.repository;

import com.jonyshev.intershop.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

@DataR2dbcTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup(@Autowired DatabaseClient databaseClient) {
        databaseClient.sql("DELETE FROM items").then().block();

        List<Item> items = List.of(
                Item.builder().title("Маргарита").description("Классическая пицца").imgPath("/images/pizza.png").price(BigDecimal.valueOf(550)).build(),
                Item.builder().title("Дьявола").description("Острая пицца").imgPath("/images/burger.png").price(BigDecimal.valueOf(350)).build(),
                Item.builder().title("Суши").description("Набор роллов").imgPath("/images/sushi.png").price(BigDecimal.valueOf(750)).build()
        );

        itemRepository.saveAll(items).collectList().block();
    }

    @Test
    void searchItems_shouldReturnMatchingItems_sortedByPrice() {
        //when
        Flux<Item> result = itemRepository.searchItems("пицца", "PRICE", 10, 0);
        //then
        StepVerifier.create(result)
                .expectNextMatches(item -> item.getTitle().equals("Дьявола"))
                .expectNextMatches(item -> item.getTitle().equals("Маргарита"))
                .verifyComplete();
    }


}
