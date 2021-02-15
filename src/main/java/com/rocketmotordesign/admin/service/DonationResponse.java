package com.rocketmotordesign.admin.service;

public class DonationResponse {
    private Long rollingMonthDonationsInCent = 0L;
    private Long currentYearDonationsInCent = 0L;

    public DonationResponse() {
    }

    public void addRollingMonthDonations(Long rollingMonthDonationsInCent){
        this.rollingMonthDonationsInCent += rollingMonthDonationsInCent;
    }

    public void addCurrentYearDonationsInCent(Long currentYearDonationsInCent){
        this.currentYearDonationsInCent += currentYearDonationsInCent;
    }

    public Long getRollingMonthDonationsInCent() {
        return rollingMonthDonationsInCent;
    }

    public Long getCurrentYearDonationsInCent() {
        return currentYearDonationsInCent;
    }
}
