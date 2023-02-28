package greencity.security.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import greencity.ModelUtils;
import greencity.entity.User;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
import greencity.service.AchievementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoogleSecurityTransactionalServiceImplTest {
    @Mock
    private JwtTool jwtTool;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AchievementService achievementService;
    @Mock
    private UserRepo userRepo;
    @Spy
    private GoogleIdToken.Payload payload;
    @InjectMocks
    GoogleSecurityTransactionalServiceImpl googleSecurityTransactionalService;

    @Test
    void signUpTest() {
        String language = "ua";
        User user = ModelUtils.getUser();
        when(userRepo.save(any(User.class))).thenReturn(user);
        User savedUser = googleSecurityTransactionalService.signUp(payload, language);
        verify(userRepo, times(1)).save(any(User.class));
        Assertions.assertNotNull(savedUser.getId());
    }
}
