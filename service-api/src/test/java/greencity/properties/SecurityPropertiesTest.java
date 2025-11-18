package greencity.properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import greencity.constant.ErrorMessage;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityPropertiesTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private SecurityProperties properties;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(SecurityProperties.class);
        logCaptor.clearLogs();
    }

    @Test
    void getAccessTokenExpiration_shouldReturnValue_whenExists() {
        when(environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class))
            .thenReturn(15);

        assertEquals(15, properties.getAccessTokenExpiration());
        assertTrue(logCaptor.getErrorLogs().isEmpty());
    }

    @Test
    void getAccessTokenExpiration_shouldThrow_whenMissing() {
        when(environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class))
            .thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getAccessTokenExpiration());

        assertEquals(ErrorMessage.ACCESS_TOKEN_EXPIRATION_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.ACCESS_TOKEN_EXPIRATION_NOT_SET));
    }

    @Test
    void getRefreshTokenExpiration_shouldReturnValue_whenExists() {
        when(environment.getProperty("security.jwt.refresh-token.expiration-minutes", Integer.class))
            .thenReturn(60);

        assertEquals(60, properties.getRefreshTokenExpiration());
    }

    @Test
    void getRefreshTokenExpiration_shouldThrow_whenMissing() {
        when(environment.getProperty("security.jwt.refresh-token.expiration-minutes", Integer.class))
            .thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getRefreshTokenExpiration());

        assertEquals(ErrorMessage.REFRESH_TOKEN_EXPIRATION_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.REFRESH_TOKEN_EXPIRATION_NOT_SET));
    }

    @Test
    void getAccessTokenKey_shouldReturnValue_whenExists() {
        when(environment.getProperty("security.jwt.secret-key"))
            .thenReturn("super-secret");

        assertEquals("super-secret", properties.getAccessTokenKey());
    }

    @Test
    void getAccessTokenKey_shouldThrow_whenMissing() {
        when(environment.getProperty("security.jwt.secret-key"))
            .thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getAccessTokenKey());

        assertEquals(ErrorMessage.ACCESS_TOKEN_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.ACCESS_TOKEN_NOT_SET));
    }

    @Test
    void getVerifyEmailExpiration_shouldReturnValue_whenExists() {
        when(environment.getProperty("security.jwt.verify-email.expiration-hours", Integer.class))
            .thenReturn(3);

        assertEquals(3, properties.getVerifyEmailExpiration());
    }

    @Test
    void getVerifyEmailExpiration_shouldThrow_whenMissing() {
        when(environment.getProperty("security.jwt.verify-email.expiration-hours", Integer.class))
            .thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getVerifyEmailExpiration());

        assertEquals(ErrorMessage.VERIFY_EMAIL_EXPIRATION_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.VERIFY_EMAIL_EXPIRATION_NOT_SET));
    }

    @Test
    void getBruteForceMaxAttempts_shouldReturnValue_whenExists() {
        when(environment.getProperty("security.brute-force.max-attempts", Integer.class))
            .thenReturn(5);

        assertEquals(5, properties.getBruteForceMaxAttempts());
    }

    @Test
    void getBruteForceMaxAttempts_shouldThrow_whenMissing() {
        when(environment.getProperty("security.brute-force.max-attempts", Integer.class))
            .thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getBruteForceMaxAttempts());

        assertEquals(ErrorMessage.BRUTEFORCE_MAX_ATTEMPTS_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.BRUTEFORCE_MAX_ATTEMPTS_NOT_SET));
    }

    @Test
    void getBruteForceBlockTime_shouldReturnValue_whenExists() {
        when(environment.getProperty("security.brute-force.block-time-minutes", Long.class))
            .thenReturn(30L);

        assertEquals(30L, properties.getBruteForceBlockTime());
    }

    @Test
    void getBruteForceBlockTime_shouldThrow_whenMissing() {
        when(environment.getProperty("security.brute-force.block-time-minutes", Long.class))
            .thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getBruteForceBlockTime());

        assertEquals(ErrorMessage.BRUTEFORCE_BLOCK_TIME_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.BRUTEFORCE_BLOCK_TIME_NOT_SET));
    }

    @Test
    void getTesterSignInToken_shouldReturnValue_whenExists() {
        when(environment.getProperty("testers.sign-in-token"))
            .thenReturn("test-token");

        assertEquals("test-token", properties.getTesterSignInToken());
    }

    @Test
    void getTesterSignInToken_shouldThrow_whenMissing() {
        when(environment.getProperty("testers.sign-in-token"))
            .thenReturn("");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> properties.getTesterSignInToken());

        assertEquals(ErrorMessage.TESTER_SIGN_IN_TOKEN_NOT_SET, ex.getMessage());
        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.TESTER_SIGN_IN_TOKEN_NOT_SET));
    }

    @Test
    void validateProperties_shouldLogSuccess_whenAllPropertiesValid() {
        when(environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class))
            .thenReturn(15);
        when(environment.getProperty("security.jwt.refresh-token.expiration-minutes", Integer.class))
            .thenReturn(60);
        when(environment.getProperty("security.jwt.secret-key"))
            .thenReturn("key");
        when(environment.getProperty("security.jwt.verify-email.expiration-hours", Integer.class))
            .thenReturn(1);
        when(environment.getProperty("security.brute-force.max-attempts", Integer.class))
            .thenReturn(5);
        when(environment.getProperty("security.brute-force.block-time-minutes", Long.class))
            .thenReturn(10L);
        when(environment.getProperty("testers.sign-in-token"))
            .thenReturn("token");

        properties.validateProperties();

        assertTrue(logCaptor.getInfoLogs()
            .contains("All security properties validated successfully."));
    }

    @Test
    void validateProperties_shouldThrow_whenAnyPropertyMissing() {
        when(environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class))
            .thenReturn(null);
        when(environment.getProperty("security.jwt.refresh-token.expiration-minutes", Integer.class))
            .thenReturn(60);
        when(environment.getProperty("security.jwt.secret-key"))
            .thenReturn("key");
        when(environment.getProperty("security.jwt.verify-email.expiration-hours", Integer.class))
            .thenReturn(1);
        when(environment.getProperty("security.brute-force.max-attempts", Integer.class))
            .thenReturn(5);
        when(environment.getProperty("security.brute-force.block-time-minutes", Long.class))
            .thenReturn(10L);
        when(environment.getProperty("testers.sign-in-token"))
            .thenReturn("token");

        assertThrows(IllegalStateException.class,
            () -> properties.validateProperties());

        assertTrue(logCaptor.getErrorLogs()
            .contains(ErrorMessage.ACCESS_TOKEN_EXPIRATION_NOT_SET));
    }
}