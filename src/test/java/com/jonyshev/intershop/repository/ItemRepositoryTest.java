package com.jonyshev.intershop.repository;

import com.jonyshev.intershop.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testFindByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase() {
        //given
        Item item1 = Item.builder()
                .title("Burger")
                .description("Delicious")
                .build();

        Item item2 = Item.builder()
                .title("Pizza")
                .description("Italian food")
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Item> result = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("burger", "burger", pageable);

        //then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Burger");
    }
}