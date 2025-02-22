package com.rocketmotordesign.admin.controller;

import com.rocketmotordesign.admin.controller.response.DonatelyDonationResponse;
import com.rocketmotordesign.admin.service.DonatelyService;
import com.rocketmotordesign.admin.service.DonationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("donations")
public class DonatelyController {

    private static final Logger LOGGER = LoggerFactory.getLogger("donations");
    private final DonatelyService donatelyService;
    private static Map<LocalDate, DonationResponse> cachedResponses = new HashMap<>();


    public DonatelyController(DonatelyService donatelyService) {
        this.donatelyService = donatelyService;
    }


    @GetMapping
    public ResponseEntity getDonations() {

        LocalDateTime now = LocalDateTime.now();
        return ok(Optional.ofNullable(cachedResponses.get(now.toLocalDate()))
                .orElseGet(() -> getDonationResponse(now)));
    }

    private DonationResponse getDonationResponse(LocalDateTime now) {
        cachedResponses.clear();
        LOGGER.info("clear donately response cache");

        List<DonatelyDonationResponse> donatelyDonationResponses = donatelyService.retrieveDonations();
        DonationResponse donationResponse = new DonationResponse();


        donatelyDonationResponses.forEach(response ->  {
            LocalDateTime donationDate = Instant.ofEpochMilli(response.getDonationDateTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            if(donationDate.getYear() == now.getYear()) {
                donationResponse.addCurrentYearDonationsInCent(response.getDonationInCent());
            }

            if(ChronoUnit.DAYS.between(donationDate, now)<=30) {
                donationResponse.addRollingMonthDonations(response.getDonationInCent());
            }
        });
        cachedResponses.put(now.toLocalDate(), donationResponse);
        LOGGER.info("add donately response to cache");

        return donationResponse;
    }

}
