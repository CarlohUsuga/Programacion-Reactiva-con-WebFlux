package com.restaurant.catalogapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    private Long id;

    private String email;

    private String password;

    private String name;

    @Builder.Default
    private String role = "USER";

    @Builder.Default
    private Boolean enabled = true;

    private LocalDateTime createdAt;
}
