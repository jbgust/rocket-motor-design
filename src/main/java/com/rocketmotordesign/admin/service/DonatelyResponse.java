package com.rocketmotordesign.admin.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rocketmotordesign.admin.controller.response.DonatelyDonationResponse;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DonatelyResponse {

    @JsonProperty("data")
    private List<DonatelyDonationResponse> donations;

    public List<DonatelyDonationResponse> getDonations() {
        return donations;
    }

    public void setDonations(List<DonatelyDonationResponse> donations) {
        this.donations = donations;
    }
}
