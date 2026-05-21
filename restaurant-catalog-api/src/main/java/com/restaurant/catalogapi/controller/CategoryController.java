package com.restaurant.catalogapi.controller;

import com.restaurant.catalogapi.dto.CategoryRequest;
import com.restaurant.catalogapi.dto.CategoryResponse;
import com.restaurant.catalogapi.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * GET /api/categories
     * Returns all categories.
     */
    @GetMapping
    public Flux<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    /**
     * GET /api/categories/{id}
     * Returns a category by ID.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CategoryResponse>> findById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok);
    }

    /**
     * POST /api/categories
     * Creates a new category. Requires authentication.
     */
    @PostMapping
    public Mono<ResponseEntity<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request)
                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(category));
    }

    /**
     * PUT /api/categories/{id}
     * Updates a category. Requires authentication.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request)
                .map(ResponseEntity::ok);
    }

    /**
     * DELETE /api/categories/{id}
     * Deletes a category. Requires authentication.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return categoryService.delete(id)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }
}
