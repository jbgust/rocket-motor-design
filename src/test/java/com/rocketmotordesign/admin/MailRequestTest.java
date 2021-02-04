package com.rocketmotordesign.admin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MailRequestTest {

    @Test
    void shouldComputePageSize() {
        assertThat(new MailRequest("subject", "content", "receiver", 10, 20).getPageSize())
                .isEqualTo(10);
    }
}