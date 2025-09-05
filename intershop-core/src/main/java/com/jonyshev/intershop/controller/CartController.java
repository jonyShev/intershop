package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.CartActionForm;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.OrderService;
import com.jonyshev.intershop.service.PaymentServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentServiceClient paymentServiceClient;

    @GetMapping("/cart/items")
    public Mono<String> getCartItems(WebSession session, Model model,
                                     @RequestParam(name = "err", required = false) String err) {
        return orderService.getItemsAndTotal(session)
                .zipWith(
                        paymentServiceClient.getBalance()
                                .map(r -> BigDecimal.valueOf(r.getAmount())) // <-- достаём сумму
                                .map(Optional::of)
                                .onErrorReturn(Optional.empty())
                )
                .map(t -> {
                    var items      = t.getT1().getT1();
                    var total      = t.getT1().getT2();
                    var balanceOpt = t.getT2();

                    BigDecimal balance = balanceOpt.orElse(null); // тут уже можно null для шаблона
                    boolean empty  = items.isEmpty();
                    boolean canBuy = !empty && balance != null && balance.compareTo(total) >= 0;

                    model.addAttribute("items", items);
                    model.addAttribute("total", total);
                    model.addAttribute("balance", balance);
                    model.addAttribute("empty", empty);
                    model.addAttribute("canBuy", canBuy);
                    if (err != null) model.addAttribute("error", err); // чтобы сообщения в UI работали

                    return "cart";
                });
    }

    @PostMapping("/cart/items/{id}")
    public Mono<String> updateCartFromCart(@PathVariable Long id,
                                           @ModelAttribute CartActionForm form,
                                           WebSession session) {
        CartAction action = CartAction.valueOf(form.getAction().toUpperCase());
        return cartService.updateCartAction(id, action, session)
                .then(Mono.just("redirect:/cart/items"));
    }

    @PostMapping("/buy")
    public Mono<String> buy(WebSession session) {
        return orderService.getItemsAndTotal(session)
                .flatMap(tuple -> {
                    var items = tuple.getT1();
                    var total = tuple.getT2();
                    return paymentServiceClient.pay(total)
                            .map(resp -> resp.getSuccess() != null && resp.getSuccess()) // превращаем в boolean
                            .flatMap(success -> {
                                if (!success) {
                                    return Mono.just("redirect:/cart/items?err=INSUFFICIENT_FUNDS");
                                }
                                return orderService.createOrder(items, total, session)
                                        .map(order -> "redirect:/orders/" + order.getId() + "?newOrder=true");
                            });
                })
                .onErrorResume(ex -> Mono.just("redirect:/cart/items?err=PAYMENT_SERVICE_UNAVAILABLE"));
    }
}
