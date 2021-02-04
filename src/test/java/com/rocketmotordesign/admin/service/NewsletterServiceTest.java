package com.rocketmotordesign.admin.service;

import com.rocketmotordesign.admin.MailRequest;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import com.rocketmotordesign.security.services.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.mail.MessagingException;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class NewsletterServiceTest {

    private NewsletterService newsletterService;

    private UserRepository userRepository;

    private MailService mailService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mailService = mock(MailService.class);
        newsletterService = new NewsletterService(userRepository, mailService);
    }

    @Test
    void shouldSendNewsletterToUsers() throws MessagingException {
        //GIVEN
        MailRequest request = new MailRequest("sujet", "content", 2, 3);
        given(userRepository.findUserByReceiveNewsletterIsTrueAndCompteValideIsTrueOrderByIdAsc())
                .willReturn(of(
                        new User("user1@domain.org", "pwd1"),
                        new User("user2@haha.fr", "pwd2"),
                        new User("user3@domain.org", "pwd1"),
                        new User("user4@haha.fr", "pwd2")));

        //WHEN
        newsletterService.sendNewsletter(request);

        //THEN
        ArgumentCaptor<String> receivers = ArgumentCaptor.forClass(String.class);
        verify(mailService, times(2))
                .sendHtmlMessage(eq(request.getSubject()), eq(request.getHtmlContent()), receivers.capture());

        assertThat(receivers.getAllValues())
                .containsExactlyInAnyOrder(
                        "user2@haha.fr",
                        "user3@domain.org"
                );

    }

}