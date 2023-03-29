package greencity.security.service;

import greencity.client.RestClient;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.repository.UserRepo;
import greencity.security.repository.VerifyEmailRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static greencity.ModelUtils.getUbsProfileCreationDto;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyEmailServiceImplTest {
    @Mock
    private VerifyEmailRepo verifyEmailRepo;
    @Mock
    private RestClient restClient;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepo userRepo;

    private final User user = User.builder()
        .id(1L)
        .userStatus(UserStatus.CREATED)
        .name("user")
        .build();

    @InjectMocks
    private VerifyEmailServiceImpl verifyEmailService;

    @Test
    void verifyByTokenNotExpiredTokenTest() {
        UbsProfileCreationDto ubsProfile = getUbsProfileCreationDto();
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setExpiryDate(LocalDateTime.MAX);
        when(verifyEmailRepo.findByTokenAndUserId(1L, "token")).thenReturn(Optional.of(verifyEmail));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UbsProfileCreationDto.class)).thenReturn(ubsProfile);
        doReturn(1L).when(restClient).createUbsProfile(ubsProfile);
        when(userRepo.save(any(User.class))).thenReturn(user);
        verifyEmailService.verifyByToken(1L, "token");
        verify(verifyEmailRepo, times(1)).deleteVerifyEmailByTokenAndUserId(1L, "token");
        verify(userRepo, times(1)).findById(1L);
        verify(restClient, times(1)).createUbsProfile(ubsProfile);
    }

    @Test
    void verifyByTokenExpiredTokenTest() {
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setExpiryDate(LocalDateTime.MIN);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(verifyEmailRepo.findByTokenAndUserId(1L, "token")).thenReturn(Optional.of(verifyEmail));
        Assertions.assertThrows(UserActivationEmailTokenExpiredException.class,
            () -> verifyEmailService.verifyByToken(1L, "token"));
    }

    /*
     * @Test void deleteAllUsersThatDidNotVerifyEmailTest() {
     * verifyEmailService.deleteAllUsersThatDidNotVerifyEmail();
     * verify(verifyEmailRepo).deleteAllUsersThatDidNotVerifyEmail(); }
     */
}
