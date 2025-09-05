package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.dto.OrderWithItemsDto;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.model.OrderItem;
import com.jonyshev.intershop.repository.OrderItemRepository;
import com.jonyshev.intershop.repository.OrderRepository;
import com.jonyshev.intershop.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final UserRepository userRepository;

    @Override
    public Mono<Order> createOrder(List<ItemDto> itemDtos, BigDecimal totalSum, WebSession session) {
        return currentUserId()
                .flatMap(uid -> {
                    Order order = new Order();
                    order.setTotalSum(totalSum);
                    order.setUserId(uid); // ВАЖНО: привязка к пользователю

                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                List<OrderItem> orderItems = itemDtos.stream()
                                        .map(itemDto -> OrderItem.builder()
                                                .orderId(savedOrder.getId())
                                                .itemId(itemDto.getId())
                                                .title(itemDto.getTitle())
                                                .description(itemDto.getDescription())
                                                .imgPath(itemDto.getImgPath())
                                                .price(itemDto.getPrice())
                                                .count(itemDto.getCount())
                                                .build())
                                        .toList();
                                return orderItemRepository.saveAll(orderItems)
                                        .then(cartService.clear(session))
                                        .thenReturn(savedOrder);
                            });
                });
    }

    @Override
    public Mono<Tuple2<List<ItemDto>, BigDecimal>> getItemsAndTotal(WebSession session) {
        Mono<List<ItemDto>> itemsDtoMono = cartService.getCartItemsDto(session);
        Mono<BigDecimal> totalMono = cartService.getTotalPrice(session);
        return Mono.zip(itemsDtoMono, totalMono);
    }

    @Override
    public Mono<Order> findById(Long orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Order not found " + orderId)));
    }

    public Mono<List<OrderWithItemsDto>> getOrderWithItems() {
        return currentUserId()
                .flatMapMany(uid -> orderRepository.findAllByUserId(uid))
                .flatMap(order -> orderItemRepository.findAllByOrderId(order.getId())
                        .collectList()
                        .map(orderItems -> OrderWithItemsDto.builder()
                                .id(order.getId())
                                .totalSum(order.getTotalSum())
                                .orderItems(orderItems)
                                .build()))
                .collectList();
    }

    private Mono<UUID> currentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())   // username
                .flatMap(userRepository::findByUsername)
                .map(user -> user.getId());
    }
}
