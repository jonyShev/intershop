package com.jonyshev.intershop.repository;

import com.jonyshev.intershop.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}