package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.model.PagingInfo;
import com.jonyshev.intershop.service.ReactiveItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReactiveItemController {

    private final ReactiveItemService reactiveItemService;

    @GetMapping("main/items")
    public Mono<String> getAllItems(@RequestParam(defaultValue = "") String search,
                                    @RequestParam(defaultValue = "NO") String sort,
                                    @RequestParam(defaultValue = "10") int pageSize,
                                    @RequestParam(defaultValue = "1") int pageNumber,
                                    Model model) {
        return reactiveItemService.getAllItems(search, sort, pageSize, pageNumber)
                .map(reactiveItemService::mapToDto)
                .collectList()
                .map(itemDtos -> reactiveItemService.chunkItems(itemDtos, 3))
                .doOnNext(chunks -> {
                    model.addAttribute("items", chunks);
                    model.addAttribute("search", search);
                    model.addAttribute("paging", new PagingInfo(
                            pageNumber,
                            pageSize,
                            chunks.stream().mapToInt(List::size).sum() == pageSize,
                            pageNumber > 1
                    ));
                })
                .thenReturn("main");
    }
}