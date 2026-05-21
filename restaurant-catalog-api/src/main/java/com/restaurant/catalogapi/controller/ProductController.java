package com.restaurant.catalogapi.controller;

import com.restaurant.catalogapi.dto.ProductRequest;
import com.restaurant.catalogapi.dto.ProductResponse;
import com.restaurant.catalogapi.service.ProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products
     * Returns all products. Supports optional filters: categoryId, available.
     */
    @GetMapping
    public Flux<ProductResponse> findAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean available) {
        return productService.findAll(categoryId, available);
    }

    /**
     * GET /api/products/category/{categoryId}
     * Returns products filtered by category.
     */
    @GetMapping("/category/{categoryId}")
    public Flux<ProductResponse> findByCategory(@PathVariable Long categoryId) {
        return productService.findByCategory(categoryId);
    }

    /**
     * GET /api/products/search?keyword=xxx
     * Searches products by name (case-insensitive).
     */
    @GetMapping("/search")
    public Flux<ProductResponse> search(@RequestParam String keyword) {
        return productService.search(keyword);
    }

    /**
     * GET /api/products/available
     * Returns only available products.
     */
    @GetMapping("/available")
    public Flux<ProductResponse> findAvailable() {
        return productService.findAvailable();
    }

    /**
     * GET /api/products/{id}
     * Returns a single product by ID.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> findById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok);
    }

    /**
     * POST /api/products
     * Creates a new product. Requires authentication.
     */
    @PostMapping
    public Mono<ResponseEntity<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }

    /**
     * PUT /api/products/{id}
     * Updates an existing product. Requires authentication.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request)
                .map(ResponseEntity::ok);
    }

    /**
     * DELETE /api/products/{id}
     * Deletes a product. Requires authentication.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return productService.delete(id)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }
}
