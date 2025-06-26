package com.jonyshev.intershop.service;

import org.springframework.stereotype.Service;

@Service
public interface CartService {

    void addItem(Long id);

    void decreaseItem(Long id);

    void deleteItem(Long id);
}