package com.rocketmotordesign.security.repository;

import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.models.UserValidationTokenType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserValidationTokenRepository extends CrudRepository<UserValidationToken, String> {

    Optional<UserValidationToken> findByIdAndTokenType(String id, UserValidationTokenType tokenType);
}
