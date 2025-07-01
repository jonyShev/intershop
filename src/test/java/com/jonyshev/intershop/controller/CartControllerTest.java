package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @Test
    void testGetCartItems() throws Exception {
        mockMvc.perform(get("/cart/items"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("cart"));
    }

    @Test
    void testUpdateCartFromCart() throws Exception {
        mockMvc.perform(post("/cart/items/1").param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));
        verify(cartService, times(1)).updateCartAction(1L, CartAction.PLUS);
    }

    @Test
    void testBuy() throws Exception {
        Long id = 1L;
        when(orderService.createOrder(any(), any())).thenReturn(new Order(id, null, null));

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1?newOrder=true"));

        verify(orderService, times(1)).createOrder(any(), any());
        verify(cartService, times(1)).clear();
    }
}