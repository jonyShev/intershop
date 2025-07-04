package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.mapper.ItemMapper;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }


    @Override
    public Page<Item> findItems(String search, String sort, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return itemRepository.findAll(pageable);
        } else {
            return itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    search, search, pageable);
        }
    }

    @Override
    public List<ItemDto> mapToDto(List<Item> items, CartService cartService) {
        return items.stream()
                .map(item -> itemMapper.toDto(item, cartService))
                .toList();
    }

    @Override
    public ItemDto mapToDto(Item item, CartService cartService) {
        return itemMapper.toDto(item, cartService);
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
    public Sort getSort(String sort) {
        return switch (sort) {
            case "ALPHA" -> Sort.by("title");
            case "PRICE" -> Sort.by("price");
            default -> Sort.unsorted();
        };
    }

    @Override
    public Pageable buildPageable(int pageNumber, int pageSize, String sort) {
        Sort resultSort = getSort(sort);
        return PageRequest.of(pageNumber - 1, pageSize, resultSort);
    }
}