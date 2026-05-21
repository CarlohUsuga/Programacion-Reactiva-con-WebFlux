package com.restaurant.catalogapi.repository;

import com.restaurant.catalogapi.model.PasswordResetToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PasswordResetTokenRepository extends ReactiveCrudRepository<PasswordResetToken, Long> {

    Mono<PasswordResetToken> findByToken(String token);

    Mono<Void> deleteByUserId(Long userId);
}
