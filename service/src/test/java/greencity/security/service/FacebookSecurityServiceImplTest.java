package greencity.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FacebookSecurityServiceImplTest {

    @InjectMocks
    FacebookSecurityServiceImpl facebookSecurityService;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(facebookSecurityService, "address", "http://localhost:8080");
        ReflectionTestUtils.setField(facebookSecurityService, "facebookAppId", "12345");
        ReflectionTestUtils.setField(facebookSecurityService, "facebookAppSecret", "6789");
    }

    @Test
    void generateFacebookAuthorizeURLTest() {
        String expected = "https://www.facebook.com/v2.5/dialog/oauth?client_id=12345&response_type=code&redirect"
            + "_uri=http%3A%2F%2Flocalhost%3A8080%2FfacebookSecurity%2Ffacebook&scope=email";
        String actual = facebookSecurityService.generateFacebookAuthorizeURL();
        assertEquals(expected, actual);
    }
}
