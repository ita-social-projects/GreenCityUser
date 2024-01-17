package greencity.security.jwt;

import static greencity.constant.AppConstant.ROLE;
import greencity.dto.user.UserVO;
import greencity.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Yurii Koval
 */
@ExtendWith(MockitoExtension.class)
class JwtToolTest {
    private final String expectedEmail = "test@gmail.com";
    private final Role expectedRole = Role.ROLE_USER;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    private JwtTool jwtTool;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(jwtTool, "accessTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTool, "refreshTokenValidTimeInMinutes", 15);
        ReflectionTestUtils.setField(jwtTool, "accessTokenKey", "12312312312312312312312312312312312");
    }

    @Test
    void createAccessToken() {
        final String accessToken = jwtTool.createAccessToken(expectedEmail, expectedRole);
        System.out.println(accessToken);

        SecretKey key = Keys.hmacShaKeyFor(jwtTool.getAccessTokenKey().getBytes());

        String actualEmail = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(accessToken)
            .getPayload()
            .getSubject();
        assertEquals(expectedEmail, actualEmail);
        @SuppressWarnings({"unchecked, rawtype"})
        List<String> authorities = (List<String>) Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(accessToken)
            .getPayload()
            .get(ROLE);
        assertEquals(expectedRole, Role.valueOf(authorities.getFirst()));
    }

    @Test
    void createRefreshToken() {
        String s = "secret-refresh-token-key-bigger-key";
        UserVO userVO = new UserVO();
        userVO.setEmail(expectedEmail);
        userVO.setRole(expectedRole);
        userVO.setRefreshTokenKey(s);
        SecretKey key = Keys.hmacShaKeyFor(userVO.getRefreshTokenKey().getBytes());
        String refreshToken = jwtTool.createRefreshToken(userVO);
        String actualEmail = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload()
            .getSubject();
        assertEquals(expectedEmail, actualEmail);
        @SuppressWarnings({"unchecked, rawtype"})
        List<String> authorities = (List<String>) Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload()
            .get(ROLE);
        assertEquals(expectedRole, Role.valueOf(authorities.getFirst()));
    }

    @Test
    void getEmailOutOfAccessToken() {
        String actualEmail = jwtTool.getEmailOutOfAccessToken("""
            eyJhbGciOiJIUzI1NiJ9\
            .eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdL\
            CJpYXQiOjE1NzU4MzY5NjUsImV4cCI6OTk5OTk5OTk5OTk5fQ\
            .YFicrqBFN0Q662HqkI2P8yuykgvJjiTgUqsUhN4ICHI""");
        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    void isTokenValidWithInvalidTokenTest() {
        String random = UUID.randomUUID().toString();
        assertFalse(jwtTool.isTokenValid(random, jwtTool.getAccessTokenKey()));
        boolean valid = jwtTool.isTokenValid("""
            eyJhbGciOiJIUzI1NiJ9\
            .eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUi\
            JdLCJpYXQiOjE1NzU4MzY5NjUsImV4cCI6MTU3NTgzNzg2NX0\
            .1kVcts6LCzUov-j0zMQqRXqIxeChUUv2gsw_zoLXtc8\
            """, jwtTool.getAccessTokenKey());
        assertFalse(valid);
    }

    @Test
    void isTokenValidWithValidTokenTest() {
        final String accessToken = """ 
            eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjpbIlJP\
            TEVfVVNFUiJdLCJpYXQiOjE1NzU4NDUzNTAsImV4cCI6NjE1NzU4NDUyOTB9.E5IaeqJNd6DGp6FG\
            YRV6rx-colw4wDD2hCYHnliRYlw\
            """;
        SecretKey key = Keys.hmacShaKeyFor(jwtTool.getAccessTokenKey().getBytes());
        Date expectedExpiration = new Date(61575845290000L); // 3921 year
        Date actualExpiration = Jwts.parser()
            .verifyWith(key).build()
            .parseSignedClaims(accessToken)
            .getPayload()
            .getExpiration();
        jwtTool.isTokenValid(accessToken, jwtTool.getAccessTokenKey());
        assertEquals(expectedExpiration, actualExpiration);
    }

    @Test
    void getTokenFromHttpServletRequest() {
        final String expectedToken = "An AccessToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expectedToken);
        String actualToken = jwtTool.getTokenFromHttpServletRequest(request);
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void generateTokenKeyTest() {
        assertNotNull(jwtTool.generateTokenKey());
    }

    @Test
    void generateTokenKeyWithCodedDateTest() {
        assertNotNull(jwtTool.generateTokenKeyWithCodedDate());
    }

    @Test
    void generateTokenKeyWithCodedDateLengthTest() {
        int tokenLength = 68;
        assertEquals(tokenLength, jwtTool.generateTokenKeyWithCodedDate().length());
    }
}
