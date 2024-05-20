package com.gussbom.auth.entities;

import com.gussbom.auth.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="appToken")
public class Token extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType type;
    private boolean expired;
    @ManyToOne
    @JoinColumn(name="user_id")
    private AppUser user;
}
