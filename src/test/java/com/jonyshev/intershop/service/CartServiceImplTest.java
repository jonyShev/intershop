/*
package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddItem() {
        cartService.addItem(1L);
        assertThat(cartService.getCountForItem(1L)).isEqualTo(1);
    }

    @Test
    public void testDecreaseItem() {
        cartService.addItem(1L);
        cartService.addItem(1L);
        cartService.addItem(1L);
        cartService.decreaseItem(1L);
        assertThat(cartService.getCountForItem(1L)).isEqualTo(2);
    }

    @Test
    public void testDeleteItem() {
        cartService.addItem(1L);
        cartService.deleteItem(1L);
        assertThat(cartService.getCountForItem(1L)).isEqualTo(0);
    }

    @Test
    public void testClearCart() {
        cartService.addItem(1L);
        cartService.addItem(2L);
        cartService.clear();
        assertThat(cartService.isEmpty()).isTrue();
    }

    @Test
    void testIsEmpty() {
        assertThat(cartService.isEmpty()).isTrue();
        cartService.addItem(1L);
        assertThat(cartService.isEmpty()).isFalse();
    }

    @Test
    void testGetCountForItem() {
        cartService.addItem(1L);
        cartService.addItem(1L);
        assertThat(cartService.getCountForItem(1L)).isEqualTo(2);
    }

    @Test
    void testGetCartItemsDto() {
        //given
        Item item = Item.builder()
                .id(1L)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .build();

        cartService.addItem(1L);

        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));
        when(itemService.mapToDto(item, cartService)).thenReturn(itemDto);

        //when
        List<ItemDto> result = cartService.getCartItemsDto();

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(itemService, times(1)).getItemById(1L);
        verify(itemService, times(1)).mapToDto(item, cartService);

    }

    @Test
    void testGetCartItems() {
        //given
        Item item = Item.builder()
                .id(1L)
                .title("burger")
                .build();

        cartService.addItem(1L);
        cartService.addItem(1L);

        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));

        //when
        List<Item> items = cartService.getCartItems();

        //then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getId()).isEqualTo(1L);
        assertThat(items.get(0).getTitle()).isEqualTo("burger");
        verify(itemService, times(1)).getItemById(1L);
    }

    @Test
    void testGetTotalPrice() {
        //given
        Item item = Item.builder()
                .id(1L)
                .title("burger")
                .price(BigDecimal.valueOf(100))
                .build();

        for (int i = 0; i < 5; i++) {
            cartService.addItem(1L);
        }

        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));

        //when
        BigDecimal result = cartService.getTotalPrice();

        //then
        assertThat(result).isEqualTo(BigDecimal.valueOf(500));
        verify(itemService, times(1)).getItemById(1L);
    }
}
*/
