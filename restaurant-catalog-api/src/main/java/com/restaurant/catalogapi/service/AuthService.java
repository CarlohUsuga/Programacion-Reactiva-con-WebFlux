package com.restaurant.catalogapi.service;

import com.restaurant.catalogapi.dto.AuthResponse;
import com.restaurant.catalogapi.dto.LoginRequest;
import com.restaurant.catalogapi.dto.MessageResponse;
import com.restaurant.catalogapi.dto.PasswordResetRequest;
import com.restaurant.catalogapi.dto.RegisterRequest;
import com.restaurant.catalogapi.dto.ResetPasswordRequest;
import com.restaurant.catalogapi.exception.DuplicateEmailException;
import com.restaurant.catalogapi.exception.InvalidTokenException;
import com.restaurant.catalogapi.model.PasswordResetToken;
import com.restaurant.catalogapi.model.User;
import com.restaurant.catalogapi.repository.PasswordResetTokenRepository;
import com.restaurant.catalogapi.repository.UserRepository;
import com.restaurant.catalogapi.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateEmailException(request.getEmail()));
                    }
                    User user = User.builder()
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .name(request.getName())
                            .build();
                    return userRepository.save(user);
                })
                .map(saved -> {
                    String token = jwtProvider.generateToken(saved.getEmail(), saved.getRole());
                    return AuthResponse.of(token, saved.getEmail(), saved.getName(), saved.getRole());
                });
    }

    public Mono<AuthResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new InvalidTokenException("Invalid email or password")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new InvalidTokenException("Invalid email or password"));
                    }
                    String token = jwtProvider.generateToken(user.getEmail(), user.getRole());
                    return Mono.just(AuthResponse.of(token, user.getEmail(), user.getName(), user.getRole()));
                });
    }

    public Mono<MessageResponse> requestPasswordReset(PasswordResetRequest request) {
        MessageResponse genericResponse = new MessageResponse("Si el correo existe, recibiras instrucciones");

        return userRepository.findByEmail(request.getEmail())
                .flatMap(user -> {
                    String resetToken = UUID.randomUUID().toString();
                    String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;

                    PasswordResetToken prt = PasswordResetToken.builder()
                            .userId(user.getId())
                            .token(resetToken)
                            .expiresAt(LocalDateTime.now().plusMinutes(30))
                            .build();

                    return passwordResetTokenRepository.deleteByUserId(user.getId())
                            .then(passwordResetTokenRepository.save(prt))
                            .then(emailService.sendPasswordResetEmail(request.getEmail(), resetLink))
                            .thenReturn(genericResponse);
                })
                .switchIfEmpty(Mono.just(genericResponse))
                .onErrorResume(e -> {
                    log.error("Error during password reset request for {}: {}", request.getEmail(), e.getMessage());
                    return Mono.just(genericResponse);
                });
    }

    public Mono<MessageResponse> resetPassword(ResetPasswordRequest request) {
        return passwordResetTokenRepository.findByToken(request.getToken())
                .switchIfEmpty(Mono.error(new InvalidTokenException("Token invalido o expirado")))
                .flatMap(prt -> {
                    if (Boolean.TRUE.equals(prt.getUsed()) || prt.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return Mono.error(new InvalidTokenException("Token invalido o expirado"));
                    }
                    return userRepository.findById(prt.getUserId())
                            .switchIfEmpty(Mono.error(new InvalidTokenException("Usuario no encontrado")))
                            .flatMap(user -> {
                                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                                prt.setUsed(true);
                                return userRepository.save(user)
                                        .then(passwordResetTokenRepository.save(prt))
                                        .thenReturn(new MessageResponse("Contrasena actualizada exitosamente"));
                            });
                });
    }
}
