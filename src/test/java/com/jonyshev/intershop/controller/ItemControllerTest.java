package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    @Test
    void testRedirectToMainItems() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    void testGetAllItems() throws Exception {
        when(itemService.buildPageable(anyInt(), anyInt(), anyString())).thenReturn(Pageable.unpaged());
        when(itemService.findItems(anyString(), anyString(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"));
    }

    @Test
    void testUpdateCartFromMain() throws Exception {
        mockMvc.perform(post("/main/items/1").param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    void testGetItemPage() throws Exception {
        Item item = Item.builder()
                .id(1L)
                .build();
        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));
        when(itemService.mapToDto(any(Item.class), any())).thenReturn(new ItemDto());

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"));
    }

    @Test
    void testUpdateCartFromItemPage() throws Exception {
        mockMvc.perform(post("/items/1").param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));
    }
}