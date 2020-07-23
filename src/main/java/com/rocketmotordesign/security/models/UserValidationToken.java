package com.rocketmotordesign.security.models;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

@Entity
@Table(name = "user_validation_token")
public class UserValidationToken {

    @Id
    private String id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "username")
    private User utilisateur;

    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private UserValidationTokenType tokenType;

    private UserValidationToken() {
    }

    public UserValidationToken(String id, User utilisateur, UserValidationTokenType tokenType, long expirationInSecodne) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.tokenType = tokenType;
        this.expiryDate = LocalDateTime.now().plus(expirationInSecodne, SECONDS);
    }

    public String getId() {
        return id;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public UserValidationTokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(UserValidationTokenType tokenType) {
        this.tokenType = tokenType;
    }
}
