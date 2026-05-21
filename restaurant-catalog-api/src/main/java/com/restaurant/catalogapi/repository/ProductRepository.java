package com.restaurant.catalogapi.repository;

import com.restaurant.catalogapi.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Flux<Product> findByCategoryId(Long categoryId);

    Flux<Product> findByAvailable(Boolean available);

    Flux<Product> findByCategoryIdAndAvailable(Long categoryId, Boolean available);

    Flux<Product> findByNameContainingIgnoreCase(String name);
}
