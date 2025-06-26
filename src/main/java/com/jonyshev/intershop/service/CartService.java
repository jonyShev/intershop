package com.jonyshev.intershop.service;

public interface CartService {

    void addItem(Long id);

    void decreaseItem(Long id);

    void deleteItem(Long id);
}