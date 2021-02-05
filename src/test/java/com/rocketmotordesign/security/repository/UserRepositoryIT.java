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
        avecToken(avecUtilisateurInvalide("user1@test.it"));
        avecUtilisateurValide("user2@test.it");

        //utilisateur a supprimer
        User userASupprimer = avecUtilisateurInvalide("userASupprimer1@test.it");

        //WHEN
        List<User> usersInvalideSansToken = userRepository.getUsersNonValideSansToken();

        //THEN
        assertThat(usersInvalideSansToken)
                .extracting(User::getEmail)
                .containsExactly(userASupprimer.getEmail());
    }

    @Test
    void shouldListUserForNewsletter() {
        avecUtilisateur("newsletter11@test.it", true, true);
        avecUtilisateur("newsletter1@test.it", true, true);
        avecUtilisateur("newsletter111@test.it", true, true);

        avecUtilisateur("newsletter2@test.it", true, false);
        avecUtilisateur("newsletter3@test.it", false, true);
        avecUtilisateur("newsletter4@test.it", false, true);

        List<User> users = userRepository.findUserByReceiveNewsletterIsTrueAndCompteValideIsTrueOrderByIdAsc();

        assertThat(users)
                .extracting(User::getEmail)
                .containsSequence(
                        "newsletter11@test.it", "newsletter1@test.it", "newsletter111@test.it")
                .doesNotContain(
                        "newsletter2@test.it",
                        "newsletter3@test.it",
                        "newsletter4@test.it");
    }

    private User avecUtilisateur(String email, boolean valid, boolean newsletter) {
        Role role = testEntityManager.find(Role.class, 1);

        User user = new User(email, "passwd");
        user.setCompteValide(valid);
        user.setReceiveNewsletter(newsletter);
        user.setRoles(Collections.singleton(role));
        testEntityManager.persistAndFlush(user);
        return user;
    }

    private User avecUtilisateurInvalide(String email) {
        return avecUtilisateur(email, false, true);
    }

    private User avecUtilisateurValide(String email) {
        return avecUtilisateur(email, true, true);
    }

    private void avecToken(User user) {
            testEntityManager.persist(new UserValidationToken(UUID.randomUUID().toString(), user, CREATION_COMPTE, 10));
    }
}
