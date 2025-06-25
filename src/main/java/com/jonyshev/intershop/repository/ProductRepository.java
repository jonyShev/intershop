package com.jonyshev.intershop.repository;

import com.jonyshev.intershop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}