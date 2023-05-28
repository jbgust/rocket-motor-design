package com.rocketmotordesign.security.repository;

import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import com.rocketmotordesign.security.models.UserValidationTokenType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;
import static com.rocketmotordesign.security.models.UserValidationTokenType.RESET_PASSWORD;
import static java.time.LocalDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserValidationTokenRepositoryIT {

    @Autowired
    private UserValidationTokenRepository userValidationTokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @Disabled("Due to fail because of H2 upgrade")
    void doitSupprimerTokenExpireAvant() {
        //GIVEN
        User user = testEntityManager.persist(new User("login@meteor.fr", "toto"));

        userValidationTokenRepository.save(createToken(user, "1", CREATION_COMPTE, parse("2020-08-10T13:15:11")));
        userValidationTokenRepository.save(createToken(user, "2", CREATION_COMPTE, parse("2020-08-10T13:15:12")));
        userValidationTokenRepository.save(createToken(user, "3", CREATION_COMPTE, parse("2020-08-10T13:15:13")));

        userValidationTokenRepository.save(createToken(user, "4", RESET_PASSWORD, parse("2020-08-10T13:15:11")));
        userValidationTokenRepository.save(createToken(user, "5", RESET_PASSWORD, parse("2020-08-10T13:15:12")));
        userValidationTokenRepository.save(createToken(user, "6", RESET_PASSWORD, parse("2020-08-10T13:15:13")));


        // WHEN
        int nbTokenSupprimes = userValidationTokenRepository.deleteAllByExpiryDateBefore(parse("2020-08-10T13:15:12"));

        //THEN
        assertThat(StreamSupport.stream(userValidationTokenRepository.findAll().spliterator(), false)
                .filter(token -> token.getUtilisateur().getId() == user.getId())
                .map(UserValidationToken::getId))
                .containsExactlyInAnyOrder("2", "3", "5", "6");

        assertThat(nbTokenSupprimes).isEqualTo(2);
    }

    @Test
    void doitrecupererUnToken() {
        //GIVEN
        User user = testEntityManager.persist(new User("user1@meteor.fr", "toto"));
        String tokenId = UUID.randomUUID().toString();
        userValidationTokenRepository.save(createToken(user, tokenId, CREATION_COMPTE, parse("2020-08-10T13:15:11")));

        // WHEN
        Optional<UserValidationToken> validationToken = userValidationTokenRepository.findByIdAndTokenType(tokenId, CREATION_COMPTE);

        testEntityManager.flush();
        testEntityManager.clear();

        //THEN
        assertThat(validationToken).isPresent();
        assertThat(userValidationTokenRepository.findByIdAndTokenType(tokenId, RESET_PASSWORD)).isNotPresent();
    }

    private UserValidationToken createToken(User user, String tokenId, UserValidationTokenType tokenType, LocalDateTime expirationDate) {
        UserValidationToken token = new UserValidationToken(tokenId, user, tokenType, 1);
        ReflectionTestUtils.setField(token, "expiryDate", expirationDate);
        return token;
    }

}
