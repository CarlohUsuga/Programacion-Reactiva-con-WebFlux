package com.restaurant.catalogapi.repository;

import com.restaurant.catalogapi.model.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

    Mono<Category> findByName(String name);

    Mono<Boolean> existsByName(String name);
}
