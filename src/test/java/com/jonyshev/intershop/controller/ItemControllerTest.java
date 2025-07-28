package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.CartActionForm;
import com.jonyshev.intershop.repository.ItemRepository;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.CartServiceImpl;
import com.jonyshev.intershop.service.ItemService;
import com.jonyshev.intershop.service.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ItemController.class)
@Import({ItemServiceImpl.class, CartServiceImpl.class})
public class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemRepository itemRepository;

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
        //mock
        when(itemService.getItemChunks(eq(""), eq("NO"), eq(10), eq(1), any()))
                .thenReturn(Mono.just(chunks));

        //when then
        webTestClient.get().uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertThat(response.getResponseBody().contains("main")));

    }

    @Test
    void updateCartFromMainTest() {
        //given
        Long id = 1L;
        CartActionForm form = new CartActionForm();
        form.setAction("PLUS");
        //mock
        when(cartService.updateCartAction(eq(id), eq(CartAction.PLUS), any())).thenReturn(Mono.empty());
        //when then
        webTestClient.post()
                .uri("/main/items/" + id)
                .body(BodyInserters.fromFormData("action", "PLUS"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");

        //verify
        verify(cartService).updateCartAction(eq(id), eq(CartAction.PLUS), any());
    }

    @Test
    void getItemPageTest() {
        //given
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .title("Sample Item")
                .description("Test description")
                .imgPath("img.jpg")
                .price(BigDecimal.valueOf(99.99))
                .count(2)
                .build();
        //mock
        when(itemService.getItemDtoById(eq(itemId), any())).thenReturn(Mono.just(itemDto));
        //when
        webTestClient.get()
                .uri("/items/" + itemId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Sample Item"));
                });
    }
}
