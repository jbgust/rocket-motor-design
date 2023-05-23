package com.rocketmotordesign.security.jwt;

import com.rocketmotordesign.security.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    private static final String JWT_SECRET = "your512BitSecret".repeat(20);
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(JWT_SECRET, 86400000, 1L);
    }

    @Test
    void doitGenererUnTokenJWT(){
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);
        given(user.isActiveDonator(any(), any())).willReturn(true);
        given(authentication.getPrincipal()).willReturn(user);


        String generateJwtToken = jwtUtils.generateJwtToken(authentication);
        assertThat(jwtUtils.validateJwtToken(generateJwtToken)).isTrue();
        Claims claims = (Claims)Jwts.parser().setSigningKey(JWT_SECRET).parse(generateJwtToken).getBody();
        assertThat(claims.get("donator")).isEqualTo(true);
        verify(user, times(1)).isActiveDonator(eq(Duration.ofDays(1L)), any());
    }

    @Test
    void doitRecupererleUsername(){
        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(new User("email", "password"));


        String generateJwtToken = jwtUtils.generateJwtToken(authentication);
        assertThat(jwtUtils.getUserNameFromJwtToken(generateJwtToken)).isEqualTo("email");
    }

    @Test
    void doitIdentifierUnTokenInvalide(){
        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(new User("email", "password"));
        String generateJwtToken = jwtUtils.generateJwtToken(authentication);

        assertThat(new JwtUtils("faussecle".repeat(30), 86400000, 1L).validateJwtToken(generateJwtToken)).isFalse();
    }

    @Test
    void doitIdentifierUnTokenExpire(){
        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(new User("email", "password"));
        JwtUtils jwtUtils1 = new JwtUtils(JWT_SECRET, 0, 1L);
        String generateJwtToken = jwtUtils1.generateJwtToken(authentication);

        assertThat(jwtUtils1.validateJwtToken(generateJwtToken)).isFalse();
    }

}
