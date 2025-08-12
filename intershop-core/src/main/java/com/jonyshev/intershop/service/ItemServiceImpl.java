package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemCacheDto;
import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CartService cartService;
    private final ItemCacheService itemCacheService;

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

    private ItemCacheDto mapToCacheDto(Item item) {
        return new ItemCacheDto(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getImgPath(),
                item.getPrice()
        );
    }

    private Mono<ItemDto> enrichWithCount(ItemCacheDto cacheDto, WebSession session) {
        return cartService.getCountForItem(cacheDto.id(), session)
                .map(count -> ItemDto.builder()
                        .id(cacheDto.id())
                        .title(cacheDto.title())
                        .description(cacheDto.description())
                        .imgPath(cacheDto.imgPath())
                        .price(cacheDto.price())
                        .count(count)
                        .build());
    }

    @Override
    public Mono<ItemDto> getItemDtoById(Long id, WebSession session) {
        // сначала пытаемся взять из кеша «чистый» ItemCacheDto, затем добавляем count из корзины
        return itemCacheService.getItem(id)
                .flatMap(cacheDto -> enrichWithCount(cacheDto, session))
                .switchIfEmpty(
                        itemRepository.findById(id)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Item not found: " + id)))
                                .flatMap(item -> {
                                    ItemCacheDto cacheDto = mapToCacheDto(item);
                                    return itemCacheService.putItem(cacheDto)
                                            .then(enrichWithCount(cacheDto, session));
                                })
                );
    }

    @Override
    public Mono<List<List<ItemDto>>> getItemChunks(String search, String sort, int pageSize, int pageNumber, WebSession session) {
        // 1) пробуем взять готовую страницу каталога из кеша (как список ItemCacheDto)
        return itemCacheService.getCatalogPage(search, sort, pageNumber, pageSize)
                .flatMap(page -> Flux.fromIterable(page.items())
                        .flatMap(dto -> enrichWithCount(dto, session))
                        .collectList()
                        .map(list -> chunkItems(list, 3))
                )
                // 2) если в кеше нет — собираем из БД, кладём в кеш «чистые» DTO и возвращаем с count
                .switchIfEmpty(
                        getAllItems(search, sort, pageSize, pageNumber)
                                .map(this::mapToCacheDto)
                                .collectList()
                                .flatMap(cacheList ->
                                        itemCacheService.putCatalogPage(search, sort, pageNumber, pageSize, cacheList)
                                                .thenMany(Flux.fromIterable(cacheList))
                                                .flatMap(dto -> enrichWithCount(dto, session))
                                                .collectList()
                                )
                                .map(list -> chunkItems(list, 3))
                );
    }
}
