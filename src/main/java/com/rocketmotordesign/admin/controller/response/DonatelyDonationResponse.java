package com.rocketmotordesign.admin.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DonatelyDonationResponse {

    @JsonProperty("donation_date")
    private long donationDateTimeStamp;

    @JsonProperty("amount_in_cents")
    private long donationInCent;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("refunded")
    private Boolean refunded;

    public long getDonationDateTimeStamp() {
        return donationDateTimeStamp;
    }

    public void setDonationDateTimeStamp(long donationDateTimeStamp) {
        this.donationDateTimeStamp = donationDateTimeStamp;
    }

    public long getDonationInCent() {
        return donationInCent;
    }

    public void setDonationInCent(long donationInCent) {
        this.donationInCent = donationInCent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getRefunded() {
        return refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }
}
