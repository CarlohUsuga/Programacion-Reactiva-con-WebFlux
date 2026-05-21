package com.restaurant.catalogapi.config;

import com.restaurant.catalogapi.model.Category;
import com.restaurant.catalogapi.model.Product;
import com.restaurant.catalogapi.repository.CategoryRepository;
import com.restaurant.catalogapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        categoryRepository.count()
                .filter(count -> count == 0)
                .flatMap(count -> seedCategories())
                .subscribe(
                        null,
                        e -> log.error("Data seeding failed: {}", e.getMessage())
                );
    }

    private Mono<Void> seedCategories() {
        List<Category> categories = List.of(
                Category.builder().name("Hamburguesas").description("Nuestras hamburguesas artesanales").build(),
                Category.builder().name("Bebidas").description("Refrescantes bebidas").build(),
                Category.builder().name("Postres").description("Dulces tentaciones").build(),
                Category.builder().name("Acompañamientos").description("El complemento perfecto").build()
        );

        return categoryRepository.saveAll(categories)
                .collectMap(Category::getName, Category::getId)
                .flatMap(this::seedProducts)
                .doOnSuccess(v -> log.info("Seed data loaded successfully"));
    }

    private Mono<Void> seedProducts(Map<String, Long> categoryIds) {
        Long hamburguesasId = categoryIds.get("Hamburguesas");
        Long bebidasId = categoryIds.get("Bebidas");
        Long postresId = categoryIds.get("Postres");
        Long acompId = categoryIds.get("Acompañamientos");

        List<Product> products = List.of(
                product("Clásica", "Hamburguesa clásica con lechuga, tomate y cheddar", "15900", hamburguesasId),
                product("BBQ", "Hamburguesa con salsa BBQ, cebolla caramelizada y tocino", "18900", hamburguesasId),
                product("Doble Carne", "Doble porción de carne con queso doble y pepinillos", "22900", hamburguesasId),

                product("Limonada", "Limonada natural con menta", "5900", bebidasId),
                product("Coca-Cola", "Coca-Cola 350ml", "4500", bebidasId),
                product("Malteada", "Malteada de vainilla, fresa o chocolate", "9900", bebidasId),

                product("Brownie", "Brownie de chocolate con nueces", "7900", postresId),
                product("Helado", "Copa de helado 2 sabores", "6500", postresId),
                product("Cheesecake", "Cheesecake de frutos rojos", "9900", postresId),

                product("Papas Fritas", "Papas fritas crujientes con sal", "6900", acompId),
                product("Aros de Cebolla", "Aros de cebolla apanados", "7900", acompId),
                product("Nuggets", "Nuggets de pollo x6 con salsa", "8900", acompId)
        );

        return productRepository.saveAll(products).then();
    }

    private Product product(String name, String description, String price, Long categoryId) {
        return Product.builder()
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .categoryId(categoryId)
                .available(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
