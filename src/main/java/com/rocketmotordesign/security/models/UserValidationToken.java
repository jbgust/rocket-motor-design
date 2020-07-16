package com.rocketmotordesign.security.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "user_validation_token")
public class UserValidationToken {

    public static final int VALIDITE_TOKEN_EN_MINUTE = 30;

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

    public UserValidationToken(String id, User utilisateur, UserValidationTokenType tokenType) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.tokenType = tokenType;
        this.expiryDate = LocalDateTime.now().plus(VALIDITE_TOKEN_EN_MINUTE, ChronoUnit.MINUTES);
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
