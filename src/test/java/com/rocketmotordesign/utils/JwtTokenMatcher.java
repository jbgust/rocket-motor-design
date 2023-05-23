package com.rocketmotordesign.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class JwtTokenMatcher implements Matcher {

    private final String jwtSecret;
    private final String email;
    private final boolean donator;

    public JwtTokenMatcher(String jwtSecret, String email, boolean donator) {
        this.jwtSecret = jwtSecret;
        this.email = email;
        this.donator = donator;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("jwt claims contains : [email: " + email + ", donator: " + donator + "]");
    }

    @Override
    public boolean matches(Object accessToken) {
        Claims claims = (Claims) Jwts.parser()
                .setSigningKey(jwtSecret)
                .parse((String) accessToken).getBody();
        if (extractEmail(claims).equals(email) &&
                extractDonator(claims) == donator) {
            return true;
        } else {
            return false;
        }
    }

    private boolean extractDonator(Claims claims) {
        return (boolean) claims.get("donator");
    }

    private String extractEmail(Claims claims) {
        return (String) claims.get("sub");
    }

    @Override
    public void describeMismatch(Object accessToken, Description description) {
        Claims claims = (Claims) Jwts.parser()
                .setSigningKey(jwtSecret)
                .parse((String) accessToken).getBody();
        description.appendText("jwt claims contains : [email: " + extractEmail(claims) + ", donator: " + extractDonator(claims) + "]");
    }

    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

    }
}
