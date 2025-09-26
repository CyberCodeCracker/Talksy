package com.amouri_dev.talksy.entities.token;

import com.amouri_dev.talksy.entities.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TOKEN", unique = true, nullable = false, length = 2048)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE", nullable = false)
    private TokenType tokenType;

    @Column(name = "ISSUED_AT")
    private LocalDateTime issuedAt;
    @Column(name = "EXPIRES_AT")
    private LocalDateTime expiresAt;
    @Column(name = "VALIDATED_AT")
    private LocalDateTime validatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

}