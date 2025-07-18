package com.jonyshev.intershop.repository;

import com.jonyshev.intershop.model.Item;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveItemRepository extends ReactiveCrudRepository<Item, Long> {
}
