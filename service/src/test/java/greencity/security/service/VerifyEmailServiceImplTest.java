package greencity.security.service;

import greencity.client.GreenCityRemoteClient;
import greencity.constant.ErrorMessage;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.GreenCityServiceException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import greencity.security.repository.VerifyEmailRepo;
import java.util.List;
import java.util.Optional;

import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static greencity.ModelUtils.getUbsProfileCreationDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class VerifyEmailServiceImplTest {
    @Mock
    VerifyEmailRepo verifyEmailRepo;

    @Mock
    ModelMapper modelMapper;

    @Mock
    UserRepo userRepo;

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @Mock
    UserService userService;

    User user = User.builder()
        .id(1L)
        .userStatus(UserStatus.CREATED)
        .name("user")
        .build();

    VerifyEmail verifyEmail = VerifyEmail.builder()
        .id(1L)
        .token("token")
        .user(user)
        .build();

    @InjectMocks
    VerifyEmailServiceImpl verifyEmailService;

    @Test
    void verifyByTokenNotExpiredTokenTest() {
        UbsProfileCreationDto ubsProfile = getUbsProfileCreationDto();

        when(verifyEmailRepo.findByTokenAndUserId("token", 1L)).thenReturn(Optional.of(verifyEmail));
        when(modelMapper.map(user, UbsProfileCreationDto.class)).thenReturn(ubsProfile);
        doReturn(1L).when(greenCityRemoteClient).createUbsProfile(ubsProfile);
        when(userRepo.save(any(User.class))).thenReturn(user);
        doNothing().when(userService).createGreenCityUser(user.getId(), null);

        verifyEmailService.verifyByToken(1L, "token");

        verify(verifyEmailRepo).deleteByTokenAndUserId("token", 1L);
        verify(greenCityRemoteClient).createUbsProfile(ubsProfile);
    }

    @Test
    void verifyWhenTokenNotExpiredAndRestClientThrowsExceptionTest() {
        String exceptionMessage = "exception message";
        String token = "token";
        Long userId = 1L;
        UbsProfileCreationDto ubsProfile = getUbsProfileCreationDto();
        boolean expectedResult = false;
        User mockUser = mock(User.class);
        VerifyEmail mockVerifyEmail = mock(VerifyEmail.class);

        when(verifyEmailRepo.findByTokenAndUserId(token, userId)).thenReturn(Optional.of(mockVerifyEmail));
        when(mockVerifyEmail.getUser()).thenReturn(mockUser);
        when(modelMapper.map(mockUser, UbsProfileCreationDto.class)).thenReturn(ubsProfile);
        when(greenCityRemoteClient.createUbsProfile(ubsProfile)).thenThrow(
            new GreenCityServiceException(exceptionMessage));

        boolean actualResult = verifyEmailService.verifyByToken(1L, "token");

        assertEquals(expectedResult, actualResult);
        verify(greenCityRemoteClient).createUbsProfile(ubsProfile);
        verify(mockUser, never()).setUserStatus(UserStatus.ACTIVATED);
        verify(userRepo, never()).save(any(User.class));
        verify(verifyEmailRepo, never()).deleteByTokenAndUserId(token, userId);
    }

    @Test
    void verifyByTokenNoTokenFoundTest() {
        String expectedExceptionMessage = ErrorMessage.VERIFICATION_TOKEN_NOT_FOUND_OR_EXPIRED;

        when(verifyEmailRepo.findByTokenAndUserId("token", 1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            verifyEmailService.verifyByToken(1L, "token");
        });

        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void removeUnusedTokensWithAccountsTest() {
        when(verifyEmailRepo.findAll()).thenReturn(List.of(verifyEmail));

        verifyEmailService.removeUnusedTokensWithAccounts();

        verify(userRepo).deleteAll(List.of(user));
    }
}
