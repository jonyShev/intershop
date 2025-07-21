/*
package com.jonyshev.intershop.service;

import com.jonyshev.intershop.mapper.ItemMapper;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetItemById() {
        //given
        Long id = 1L;
        Item item = Item.builder()
                .id(id)
                .title("Burger")
                .build();

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        //when
        Optional<Item> result = itemService.getItemById(id);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getTitle()).isEqualTo(item.getTitle());

        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    void testFindItemsWhenSearchNotNull() {
        //given
        String search = "burger";
        String sort = "ALPHA";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sort));

        Item item = Item.builder()
                .id(1L)
                .title("burger")
                .build();

        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable)).thenReturn(page);

        //when
        Page<Item> result = itemService.findItems(search, sort, pageable);

        //then
        assertThat(result.getContent()).hasSize(1);
        verify(itemRepository, times(1)).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
    }

    @Test
    void testFindItemsWhenSearchNull() {
        //given
        String sort = "ALPHA";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sort));

        Item item = Item.builder()
                .id(1L)
                .title("burger")
                .build();

        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.findAll(pageable)).thenReturn(page);

        //when
        Page<Item> result = itemService.findItems(null, sort, pageable);

        //then
        assertThat(result.getContent()).hasSize(1);
        verify(itemRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetSort() {
        //when
        Sort noSort = itemService.getSort("NO");
        Sort alphaSort = itemService.getSort("ALPHA");
        Sort priceSort = itemService.getSort("PRICE");

        //then
        assertThat(noSort.isSorted()).isFalse();
        assertThat(alphaSort).isEqualTo(Sort.by("title"));
        assertThat(priceSort).isEqualTo(Sort.by("price"));
    }

    //public Pageable buildPageable(int pageNumber, int pageSize, String sort) {
    @Test
    void testBuildPageable() {
        //given
        int pageNumber = 2;
        int pageSize = 10;
        String sort = "PRICE";

        //when
        Pageable pageable = itemService.buildPageable(pageNumber, pageSize, sort);

        //then
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber - 1);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort()).isEqualTo(Sort.by("price"));
    }

}*/
