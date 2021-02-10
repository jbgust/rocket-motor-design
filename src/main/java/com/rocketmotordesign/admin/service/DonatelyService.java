package com.rocketmotordesign.admin.service;

import com.rocketmotordesign.admin.controller.response.DonatelyDonationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class DonatelyService {

    private final String donatelyAPIKey;
    private final String donatelyBaseUrl;
    private final WebClient client;
    private final String donatelyAccountId;
    private final String donatelyCampaignId;

    public DonatelyService(@Value("${donately.api.key}")  String donatelyAPIKey,
                           @Value("${donately.base.url}")  String donatelyBaseUrl,
                           @Value("${donately.account.id}")  String donatelyAccountId,
                           @Value("${donately.campaign.id}")  String donatelyCampaignId) {
        this.donatelyAPIKey = donatelyAPIKey;
        this.donatelyBaseUrl = donatelyBaseUrl;
        client = WebClient.builder()
                .baseUrl(donatelyBaseUrl)
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                //TOKEN API DONATELY should be converted to base 64
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + donatelyAPIKey)
                .build();

        this.donatelyAccountId = donatelyAccountId;
        this.donatelyCampaignId = donatelyCampaignId;
    }

    public List<DonatelyDonationResponse> retrieveDonations() {
        return client
                .get()
                .uri(
                        "/donations?account_id={accountId}&campaign_id={campaignId}",
                        donatelyAccountId,
                        donatelyCampaignId)
                .retrieve()
                .bodyToMono(DonatelyResponse.class)
                .map(DonatelyResponse::getDonations)
                .block();
    }

}
