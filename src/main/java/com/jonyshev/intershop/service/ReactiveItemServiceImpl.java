package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ReactiveItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactiveItemServiceImpl implements ReactiveItemService {

    private final ReactiveItemRepository itemRepository;
    private final CartService cartService;

    @Override
    public Flux<Item> getAllItems(String search, String sort, int pageSize, int pageNumber) {
        int offset = (pageNumber - 1) * pageSize;
        return itemRepository.searchItems(search, sort, pageSize, offset);
    }

    @Override
    public List<List<ItemDto>> chunkItems(List<ItemDto> items, int rowSize) {
        List<List<ItemDto>> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i += rowSize) {
            result.add(items.subList(i, Math.min(i + rowSize, items.size())));
        }
        return result;
    }

    @Override
    public Mono<ItemDto> mapToDto(Item item, WebSession session) {
        return cartService.getCountForItem(item.getId(), session)
                .map(count -> ItemDto.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .imgPath(item.getImgPath())
                        .price(item.getPrice())
                        .count(count)
                        .build());
    }
}
