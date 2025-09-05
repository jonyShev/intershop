package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ItemController.class)
@ActiveProfiles("test")
class ItemControllerTest {

    @TestConfiguration
    static class NoSecurity {
        @Bean
        SecurityWebFilterChain securityWebFilterChain(
                org.springframework.security.config.web.server.ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.anyExchange().permitAll())
                    .build();
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    @Test
    void redirectToMainItems() {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");
    }

    @Test
    void getAllItemsTest() {
        // given
        List<List<ItemDto>> chunks = List.of(
                List.of(new ItemDto()),
                List.of(new ItemDto())
        );
        // mock
        when(itemService.getItemChunks(eq(""), eq("NO"), eq(10), eq(1), any()))
                .thenReturn(Mono.just(chunks));

        // when/then
        webTestClient.get().uri("/main/items")
                .exchange()
                .expectStatus().isOk();

        // verify
        verify(itemService).getItemChunks(eq(""), eq("NO"), eq(10), eq(1), any());
    }

    @Test
    void updateCartFromMainTest() {
        // given
        Long id = 1L;
        // mock
        when(cartService.updateCartAction(eq(id), eq(CartAction.PLUS), any())).thenReturn(Mono.empty());

        // when/then
        webTestClient.post()
                .uri("/main/items/" + id)
                .body(BodyInserters.fromFormData("action", "PLUS"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");

        // verify
        verify(cartService).updateCartAction(eq(id), eq(CartAction.PLUS), any());
    }

    @Test
    void getItemPageTest() {
        // given
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .title("Sample Item")
                .description("Test description")
                .imgPath("img.jpg")
                .price(BigDecimal.valueOf(99.99))
                .count(2)
                .build();

        // mock
        when(itemService.getItemDtoById(eq(itemId), any())).thenReturn(Mono.just(itemDto));

        // when/then
        webTestClient.get()
                .uri("/items/" + itemId)
                .exchange()
                .expectStatus().isOk();

        // verify
        verify(itemService).getItemDtoById(eq(itemId), any());
    }
}
