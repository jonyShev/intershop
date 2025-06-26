package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public List<Item> getAllProducts() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> getProductById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Item saveProduct(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public void deleteProduct(Long id) {
        itemRepository.deleteById(id);
    }
}