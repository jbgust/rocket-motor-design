package com.rocketmotordesign.security.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private Duration durationOfActiveDonation;

    @BeforeEach
    void setUp() {
        durationOfActiveDonation = Duration.of(365, DAYS);
    }

    @Test
    void shouldIdentifyOldDonatorAsActiveDonator() {
        User user = new User("user1", "pwd");
        user.setDonator(true);
        user.setLastDonation(null);

        assertThat(user.isActiveDonator(durationOfActiveDonation, LocalDateTime.now())).isTrue();
    }

    @Test
    void shouldIdentifyAsActiveDonator() {
        User user = new User("user1", "pwd");
        user.setDonator(false);
        LocalDateTime now = LocalDateTime.now();
        user.setLastDonation(now);

        assertThat(user.isActiveDonator(durationOfActiveDonation, now)).isTrue();
        assertThat(user.isActiveDonator(durationOfActiveDonation, now.plusDays(365))).isTrue();
    }

    @Test
    void shouldIdentifyAsActiveDonatorWithDateInFuture() {
        User user = new User("user1", "pwd");
        user.setDonator(false);
        LocalDateTime now = LocalDateTime.now();
        user.setLastDonation(now.plusDays(50));

        assertThat(user.isActiveDonator(Duration.of(365, DAYS), now)).isTrue();
    }

    @Test
    void shouldNotIdentifyTooOldDonationAsActiveDonator() {
        User user = new User("user1", "pwd");
        user.setDonator(false);
        LocalDateTime now = LocalDateTime.now();
        user.setLastDonation(now);

        assertThat(user.isActiveDonator(
                durationOfActiveDonation,
                now.plusDays(366)))
                .isFalse();
    }

    @Test
    void shouldIdentifyTooOldDonationAsActiveDonatorWhenIsDonator() {
        User user = new User("user1", "pwd");
        user.setDonator(true);
        LocalDateTime now = LocalDateTime.now();
        user.setLastDonation(now);

        assertThat(user.isActiveDonator(
                durationOfActiveDonation,
                now.plusDays(380)))
                .isTrue();
    }

    @Test
    void shouldIdentifyNewUserAsDonatorDuring7Days() {
        User user = new User("user1", "pwd");
        user.setDonator(false);
        LocalDateTime now = LocalDateTime.now();
        user.setLastDonation(null);
        user.setDateCreation(now);

        assertThat(user.isActiveDonator(
                durationOfActiveDonation,
                now))
                .isTrue();

        user.setDateCreation(now.plusDays(7));
        assertThat(user.isActiveDonator(
                durationOfActiveDonation,
                now))
                .isTrue();

        user.setDateCreation(now.plusDays(8));
        assertThat(user.isActiveDonator(
                durationOfActiveDonation,
                now))
                .isFalse();
    }
}