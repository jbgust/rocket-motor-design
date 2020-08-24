package com.rocketmotordesign.security.repository;

import com.rocketmotordesign.security.models.Role;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.models.UserValidationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.rocketmotordesign.security.models.UserValidationTokenType.CREATION_COMPTE;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    void doitSupprimerLesUtilisateursNonValide() {
        //GIVEN

        //utilisateur a conserver
        avecUtilisateurInvalide("user1@test.it", true);
        avecUtilisateurValide("user2@test.it");

        //utilisateur a supprimer
        User userASupprimer = avecUtilisateurInvalide("userASupprimer1@test.it", false);

        //WHEN
        List<User> usersinvalideSansToken = userRepository.getUsersNonValideSansToken();

        //THEN
        assertThat(usersinvalideSansToken)
                .extracting(User::getEmail)
                .containsExactly(userASupprimer.getEmail());

    }

    private User avecUtilisateurInvalide(String email, boolean avecToken) {
        Role role = testEntityManager.find(Role.class, 1);

        User user = new User(email, "passwd");
        user.setCompteValide(false);
        user.setRoles(Collections.singleton(role));
        testEntityManager.persistAndFlush(user);

        if(avecToken) {
            testEntityManager.persist(new UserValidationToken(UUID.randomUUID().toString(), user, CREATION_COMPTE, 10));
        }
        return user;
    }

    private User avecUtilisateurValide( String email) {
        Role role = testEntityManager.find(Role.class, 1);

        User user = new User(email, "passwd");
        user.setCompteValide(true);
        user.setRoles(Collections.singleton(role));
        testEntityManager.persistAndFlush(user);
        return user;
    }

}
