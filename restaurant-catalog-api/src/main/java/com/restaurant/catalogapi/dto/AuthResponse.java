package com.restaurant.catalogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private String email;
    private String name;
    private String role;

    public static AuthResponse of(String token, String email, String name, String role) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(email)
                .name(name)
                .role(role)
                .build();
    }
}
