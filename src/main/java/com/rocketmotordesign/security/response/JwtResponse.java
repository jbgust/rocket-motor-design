package com.rocketmotordesign.security.response;

public class JwtResponse {
	private final String token;

	public JwtResponse(String accessToken) {
		this.token = accessToken;
	}

	public String getAccessToken() {
		return token;
	}


}
