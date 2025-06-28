package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.mapper.ItemMapper;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
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
}