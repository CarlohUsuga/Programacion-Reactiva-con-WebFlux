package com.restaurant.catalogapi.service;

import com.restaurant.catalogapi.dto.CategoryRequest;
import com.restaurant.catalogapi.dto.CategoryResponse;
import com.restaurant.catalogapi.exception.ResourceNotFoundException;
import com.restaurant.catalogapi.model.Category;
import com.restaurant.catalogapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Flux<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .map(this::toResponse);
    }

    public Mono<CategoryResponse> findById(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category", id)))
                .map(this::toResponse);
    }

    public Mono<CategoryResponse> create(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();
        return categoryRepository.save(category)
                .map(this::toResponse);
    }

    public Mono<CategoryResponse> update(Long id, CategoryRequest request) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category", id)))
                .flatMap(existing -> {
                    existing.setName(request.getName());
                    existing.setDescription(request.getDescription());
                    existing.setImageUrl(request.getImageUrl());
                    return categoryRepository.save(existing);
                })
                .map(this::toResponse);
    }

    public Mono<Void> delete(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category", id)))
                .flatMap(categoryRepository::delete);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .build();
    }
}
