package com.rocketmotordesign.admin.controller;

import com.rocketmotordesign.admin.controller.response.DonatelyDonationResponse;
import com.rocketmotordesign.admin.service.DonationResponse;
import com.rocketmotordesign.admin.service.DonatelyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("donations")
public class DonatelyController {

    private final DonatelyService donatelyService;

    public DonatelyController(DonatelyService donatelyService) {
        this.donatelyService = donatelyService;
    }


    @GetMapping
    public ResponseEntity getDonations() {

        List<DonatelyDonationResponse> donatelyDonationResponses = donatelyService.retrieveDonations();
        DonationResponse donationResponse = new DonationResponse();

        LocalDateTime now = LocalDateTime.now();

        donatelyDonationResponses.forEach(response ->  {
            LocalDateTime donationDate = Instant.ofEpochMilli(response.getDonationDateTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            if(donationDate.getYear() == now.getYear()) {
                donationResponse.addCurrentYearDonationsInCent(response.getDonationInCent());
            }

            if(ChronoUnit.DAYS.between(donationDate, now)<=30) {
                donationResponse.addRollingMonthDonations(response.getDonationInCent());
            }
        });

        return ok(donationResponse);
    }

}
