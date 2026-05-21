package com.restaurant.catalogapi.service;

import com.restaurant.catalogapi.dto.ProductRequest;
import com.restaurant.catalogapi.dto.ProductResponse;
import com.restaurant.catalogapi.exception.ResourceNotFoundException;
import com.restaurant.catalogapi.model.Product;
import com.restaurant.catalogapi.repository.CategoryRepository;
import com.restaurant.catalogapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Flux<ProductResponse> findAll(Long categoryId, Boolean available) {
        Flux<Product> products;
        if (categoryId != null && available != null) {
            products = productRepository.findByCategoryIdAndAvailable(categoryId, available);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else if (available != null) {
            products = productRepository.findByAvailable(available);
        } else {
            products = productRepository.findAll();
        }
        return products.flatMap(this::toResponse);
    }

    public Mono<ProductResponse> findById(Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", id)))
                .flatMap(this::toResponse);
    }

    public Flux<ProductResponse> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .flatMap(this::toResponse);
    }

    public Flux<ProductResponse> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .flatMap(this::toResponse);
    }

    public Flux<ProductResponse> findAvailable() {
        return productRepository.findByAvailable(true)
                .flatMap(this::toResponse);
    }

    public Mono<ProductResponse> create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .categoryId(request.getCategoryId())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .createdAt(LocalDateTime.now())
                .build();
        return productRepository.save(product)
                .flatMap(this::toResponse);
    }

    public Mono<ProductResponse> update(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", id)))
                .flatMap(existing -> {
                    existing.setName(request.getName());
                    existing.setDescription(request.getDescription());
                    existing.setPrice(request.getPrice());
                    existing.setImageUrl(request.getImageUrl());
                    existing.setCategoryId(request.getCategoryId());
                    if (request.getAvailable() != null) {
                        existing.setAvailable(request.getAvailable());
                    }
                    return productRepository.save(existing);
                })
                .flatMap(this::toResponse);
    }

    public Mono<Void> delete(Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", id)))
                .flatMap(productRepository::delete);
    }

    private Mono<ProductResponse> toResponse(Product product) {
        if (product.getCategoryId() == null) {
            return Mono.just(buildResponse(product, null));
        }
        return categoryRepository.findById(product.getCategoryId())
                .map(cat -> buildResponse(product, cat.getName()))
                .defaultIfEmpty(buildResponse(product, null));
    }

    private ProductResponse buildResponse(Product product, String categoryName) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .available(product.getAvailable())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
