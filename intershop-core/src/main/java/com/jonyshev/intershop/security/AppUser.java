package com.jonyshev.intershop.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.util.UUID;

@Table("app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppUser {
    @Id
    private UUID id;
    private String username;
    private String password;
    private boolean enabled = true;

    // getters/setters/constructors
}
