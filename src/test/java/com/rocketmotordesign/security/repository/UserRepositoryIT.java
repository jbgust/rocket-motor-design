package com.rocketmotordesign.security.repository;

import com.rocketmotordesign.security.models.Role;
import com.rocketmotordesign.security.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static java.time.LocalDateTime.parse;
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
        User user1 = avecUtilisateurInvalide(parse("2020-08-04T12:56:45"), "user1@test.it");
        User user2 = avecUtilisateurInvalide(parse("2020-08-04T12:56:46"), "user2@test.it");
        User user3 = avecUtilisateurValide(parse("2020-08-04T12:56:45"), "user3@test.it");
        User user4 = avecUtilisateurValide(parse("2020-08-04T12:56:46"), "user4@test.it");
        User user5 = avecUtilisateurInvalide(parse("2020-08-04T12:56:45"), "user5@test.it");

        //utilisateur a supprimer
        User userASupprimer1 = avecUtilisateurInvalide(parse("2020-08-04T12:56:44"), "userASupprimer1@test.it");

        //WHEN
        userRepository.deleteAllByCompteValideFalseAndDateCreationBefore(parse("2020-08-04T12:56:45"));

        //THEN
        assertThat(userRepository.findAll())
                .extracting(User::getEmail)
                .doesNotContain(userASupprimer1.getEmail())
                .contains(
                        user1.getEmail(),
                        user2.getEmail(),
                        user3.getEmail(),
                        user4.getEmail(),
                        user5.getEmail()
                );

    }

    private User avecUtilisateurInvalide(LocalDateTime dateCreation, String email) {
        Role role = testEntityManager.find(Role.class, 1);

        User user = new User(email, "passwd");
        user.setCompteValide(false);
        user.setRoles(Collections.singleton(role));
        testEntityManager.persistAndFlush(user);
        user.setDateCreation(dateCreation);
        testEntityManager.persistAndFlush(user);

        return user;
    }

    private User avecUtilisateurValide(LocalDateTime dateCreation, String email) {
        Role role = testEntityManager.find(Role.class, 1);

        User user = new User(email, "passwd");
        user.setCompteValide(true);
        user.setDateCreation(dateCreation);
        user.setRoles(Collections.singleton(role));
        testEntityManager.persistAndFlush(user);
        user.setDateCreation(dateCreation);
        testEntityManager.persistAndFlush(user);
        return user;
    }

}
