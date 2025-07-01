package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void testShowOrder() throws Exception {
        when(orderService.findById(1L)).thenReturn(Optional.of(new Order()));

        mockMvc.perform(get("/orders/1").param("newOrder", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"));
        verify(orderService, times(1)).findById(1L);
    }

    @Test
    void testShowOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(new Order()));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"));
        verify(orderService, times(1)).getAllOrders();
    }
}