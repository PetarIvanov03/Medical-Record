package com.ivanovp.medical_record.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long ONE_HOUR_MS = 3_600_000L;

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(TEST_SECRET, ONE_HOUR_MS);
    }

    private UserDetails buildUserDetails(String username, String role) {
        return new User(username, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    @Test
    void generateToken_returnsNonBlankToken() {
        // Arrange
        UserDetails userDetails = buildUserDetails("doctor1", "DOCTOR");

        // Act
        String token = jwtTokenProvider.generateToken(userDetails);

        // Assert
        assertThat(token).isNotBlank();
    }

    @Test
    void getUsernameFromToken_returnsCorrectUsername() {
        // Arrange
        UserDetails userDetails = buildUserDetails("doctor1", "DOCTOR");
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertThat(username).isEqualTo("doctor1");
    }

    @Test
    void getExpirationDateFromToken_returnsFutureDate() {
        // Arrange
        UserDetails userDetails = buildUserDetails("patient1", "PATIENT");
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);

        // Assert
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void validateToken_withValidToken_returnsTrue() {
        // Arrange
        UserDetails userDetails = buildUserDetails("admin", "ADMIN");
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        boolean result = jwtTokenProvider.validateToken(token);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void validateToken_withExpiredToken_returnsFalse() {
        // Arrange - use negative expiration to produce an already-expired token
        JwtTokenProvider expiredProvider = new JwtTokenProvider(TEST_SECRET, -3_600_000L);
        UserDetails userDetails = buildUserDetails("doctor1", "DOCTOR");
        String expiredToken = expiredProvider.generateToken(userDetails);

        // Act
        boolean result = jwtTokenProvider.validateToken(expiredToken);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validateToken_withMalformedToken_returnsFalse() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";

        // Act
        boolean result = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validateToken_withWrongSignatureToken_returnsFalse() {
        // Arrange - token signed with a different secret
        JwtTokenProvider otherProvider = new JwtTokenProvider(
                "3274357638792F423F4528482B4C6251645468576D5A7134743777397A244326", ONE_HOUR_MS);
        UserDetails userDetails = buildUserDetails("doctor1", "DOCTOR");
        String tokenFromOtherProvider = otherProvider.generateToken(userDetails);

        // Act - validate with the original provider (different key)
        boolean result = jwtTokenProvider.validateToken(tokenFromOtherProvider);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void generateToken_differentUsersProduceDifferentTokens() {
        // Arrange
        UserDetails user1 = buildUserDetails("doctor1", "DOCTOR");
        UserDetails user2 = buildUserDetails("patient1", "PATIENT");

        // Act
        String token1 = jwtTokenProvider.generateToken(user1);
        String token2 = jwtTokenProvider.generateToken(user2);

        // Assert
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtTokenProvider.getUsernameFromToken(token1)).isEqualTo("doctor1");
        assertThat(jwtTokenProvider.getUsernameFromToken(token2)).isEqualTo("patient1");
    }
}
