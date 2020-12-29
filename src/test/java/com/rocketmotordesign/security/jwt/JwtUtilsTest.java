package com.rocketmotordesign.security.jwt;

import com.rocketmotordesign.security.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class JwtUtilsTest {


    private static final String JWT_SECRET = "your-512-bit-secret";
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(JWT_SECRET, 86400000);
    }

    @Test
    void doitGenererUnTokenJWT(){
        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(new User("email", "password"));


        String generateJwtToken = jwtUtils.generateJwtToken(authentication);
        assertThat(jwtUtils.validateJwtToken(generateJwtToken)).isTrue();
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

        assertThat(new JwtUtils("fausse-cle", 86400000).validateJwtToken(generateJwtToken)).isFalse();
    }

    @Test
    void doitIdentifierUnTokenExpire(){
        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(new User("email", "password"));
        JwtUtils jwtUtils1 = new JwtUtils(JWT_SECRET, 0);
        String generateJwtToken = jwtUtils1.generateJwtToken(authentication);

        assertThat(jwtUtils1.validateJwtToken(generateJwtToken)).isFalse();
    }

}
