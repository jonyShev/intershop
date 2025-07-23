package com.jonyshev.intershop.repository;

import com.jonyshev.intershop.model.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {

    @Query("""
                SELECT * FROM items
                WHERE LOWER(title) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(description) LIKE LOWER(CONCAT('%', :search, '%'))
                ORDER BY 
                  CASE WHEN :sort = 'PRICE' THEN price END,
                  CASE WHEN :sort = 'ALPHA' THEN title END
                LIMIT :limit OFFSET :offset
            """)
    Flux<Item> searchItems(@Param("search") String search,
                           @Param("sort") String sort,
                           @Param("limit") int limit,
                           @Param("offset") int offset);
}
