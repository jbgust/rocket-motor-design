package com.rocketmotordesign.admin.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class DonatelyControllerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    @Disabled("Only to test connection to donately API. to use it use profile jasypt and set the jasypt password")
    void shouldReturnDonations() throws Exception {
        // WHEN
        ResultActions resultActions = mvc.perform(get("/donations")
                .contentType(APPLICATION_JSON));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rollingMonthDonationsInCent", is(1706)))
                .andExpect(jsonPath("$.currentYearDonationsInCent", is(2812)));
    }

}