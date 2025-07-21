package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ReactiveItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactiveItemServiceImpl implements ReactiveItemService {

    private final ReactiveItemRepository itemRepository;

    @Override
    public Flux<Item> getAllItems(String search, String sort, int pageSize, int pageNumber) {
        int offset = (pageNumber - 1) * pageSize;
        return itemRepository.searchItems(search, sort, pageSize, offset);
    }

    /*@Override
    public ItemDto mapToDto(Item item, CartService cartService) {
        return itemMapper.toDto(item, cartService);
    }*/

    @Override
    public ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .imgPath(item.getImgPath())
                .price(item.getPrice())
                .count(0) // корзина пока отключена
                .build();
    }

    @Override
    public List<List<ItemDto>> chunkItems(List<ItemDto> items, int rowSize) {
        List<List<ItemDto>> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i += rowSize) {
            result.add(items.subList(i, Math.min(i + rowSize, items.size())));
        }
        return result;
    }
}
