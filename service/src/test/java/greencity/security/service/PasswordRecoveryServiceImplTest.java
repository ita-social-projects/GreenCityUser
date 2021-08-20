package greencity.security.service;

import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserActivationEmailTokenExpiredException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static greencity.ModelUtils.*;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceImplTest {
    @Mock
    private JwtTool jwtTool;
    @Mock
    private RestorePasswordEmailRepo restorePasswordEmailRepo;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private UserRepo userRepo;
    @Mock
    private EmailService emailService;
    @Mock
    private OwnSecurityRepo ownSecurityRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private PasswordRecoveryServiceImpl passwordRecoveryService;

    @Test
    void sendPasswordRecoveryEmailToNonExistentUserTest() {
        String email = "foo";
        String language = "en";
        when(userRepo.findByEmail(email)).thenReturn(empty());
        assertThrows(NotFoundException.class,
            () -> passwordRecoveryService.sendPasswordRecoveryEmailTo(email, language));
    }

    @Test
    void sendPasswordRecoveryEmailToUserWithExistentRestorePasswordEmailTest() {
        String email = "foo";
        String language = "en";
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(
            User.builder().restorePasswordEmail(new RestorePasswordEmail()).build()));
        assertThrows(WrongEmailException.class,
            () -> passwordRecoveryService.sendPasswordRecoveryEmailTo(email, language));
    }

    @Test
    void sendPasswordRecoveryEmailToSimpleTest() {
        String email = "foo";
        String language = "en";
        User user = new User();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        String token = "bar";
        when(jwtTool.generateTokenKeyWithCodedDate()).thenReturn(token);
        ReflectionTestUtils.setField(passwordRecoveryService, "tokenExpirationTimeInHours", 24);
        passwordRecoveryService.sendPasswordRecoveryEmailTo(email, language);
        verify(restorePasswordEmailRepo).save(refEq(
            RestorePasswordEmail.builder()
                .user(user)
                .token(token)
                .build(),
            "expiryDate"));
        verify(emailService).sendRestoreEmail(
            refEq(user.getId()),
            refEq(user.getName()),
            refEq(user.getEmail()),
            refEq(token),
            refEq(language));
    }
//
//    @Test
//    void updatePasswordUsingTokenWithNonExistentTokenTest() {
//        String token = "foo";
//        String newPassword = "bar";
//        when(restorePasswordEmailRepo.findByToken(token)).thenReturn(Optional.empty());
//        Assertions
//            .assertThrows(BadVerifyEmailTokenException.class,
//                () -> passwordRecoveryService.updatePasswordUsingToken(token, newPassword));
//    }

//    @Test
//    void updatePasswordUsingTokenWithExpiredTokenTest() {
//        String token = "foo";
//        String newPassword = "bar";
//        when(restorePasswordEmailRepo.findByToken(token)).thenReturn(Optional.of(
//            RestorePasswordEmail.builder()
//                .expiryDate(LocalDateTime.now().minusHours(1))
//                .user(User.builder().email("foo@bar.com").build())
//                .build()));
//        Assertions
//            .assertThrows(UserActivationEmailTokenExpiredException.class,
//                () -> passwordRecoveryService.updatePasswordUsingToken(token, newPassword));
//    }

//    @Test
//    void updatePasswordUsingTokenSimpleTest() {
//        String token = "foo";
//        String newPassword = "bar";
//        User user = User.builder().id(1L).userStatus(UserStatus.CREATED).email("foo@bar.com").build();
//        when(restorePasswordEmailRepo.findByToken(token)).thenReturn(Optional.of(
//            RestorePasswordEmail.builder()
//                .expiryDate(LocalDateTime.now().plusHours(1))
//                .user(user)
//                .token(token)
//                .build()));
//        passwordRecoveryService.updatePasswordUsingToken(token, newPassword);
//        verify(restorePasswordEmailRepo).delete(refEq(
//            RestorePasswordEmail.builder()
//                .user(user)
//                .token(token)
//                .build(),
//            "expiryDate"));
//        verify(applicationEventPublisher).publishEvent(refEq(
//            new UpdatePasswordEvent(passwordRecoveryService.getClass(), newPassword, user.getId()), "timestamp"));
//    }

    @Test
    void deleteAllExpiredPasswordResetTokensTest() {
        passwordRecoveryService.deleteAllExpiredPasswordResetTokens();
        verify(restorePasswordEmailRepo).deleteAllExpiredPasswordResetTokens();
    }

    @Test
    void testUpdatePasswordUsingToken() {
        when(restorePasswordEmailRepo.findByToken(TEST_OWN_RESTORE_DTO.getToken()))
            .thenReturn(ofNullable(TEST_RESTORE_PASSWORD_EMAIL));
        when(passwordEncoder.encode(TEST_OWN_RESTORE_DTO.getPassword())).thenReturn("test23");
        when(ownSecurityRepo.findByUserId(2L)).thenReturn(ofNullable(TEST_OWN_SECURITY));
        when(ownSecurityRepo.save(TEST_OWN_SECURITY)).thenReturn(TEST_OWN_SECURITY);
        doNothing().when(applicationEventPublisher).publishEvent(any());
        doNothing().when(restorePasswordEmailRepo).delete(TEST_RESTORE_PASSWORD_EMAIL);

        passwordRecoveryService.updatePasswordUsingToken(TEST_OWN_RESTORE_DTO);

        verify(restorePasswordEmailRepo).findByToken(TEST_OWN_RESTORE_DTO.getToken());
        verify(passwordEncoder).encode(TEST_OWN_RESTORE_DTO.getPassword());
        verify(ownSecurityRepo).findByUserId(2L);
        verify(ownSecurityRepo).save(TEST_OWN_SECURITY);
        verify(applicationEventPublisher).publishEvent(any());
        verify(restorePasswordEmailRepo).delete(TEST_RESTORE_PASSWORD_EMAIL);
    }

    @Test
    void testUpdatePasswordUsingGoogleToken() {
        when(restorePasswordEmailRepo.findByToken(TEST_OWN_RESTORE_DTO.getToken()))
            .thenReturn(ofNullable(TEST_RESTORE_PASSWORD_EMAIL));
        when(passwordEncoder.encode(TEST_OWN_RESTORE_DTO.getPassword())).thenReturn("test23");
        when(ownSecurityRepo.findByUserId(2L)).thenReturn(empty());
        when(userRepo.findById(2L)).thenReturn(ofNullable(TEST_USER));
        doNothing().when(applicationEventPublisher).publishEvent(any());
        doNothing().when(restorePasswordEmailRepo).delete(TEST_RESTORE_PASSWORD_EMAIL);

        passwordRecoveryService.updatePasswordUsingToken(TEST_OWN_RESTORE_DTO);

        verify(restorePasswordEmailRepo).findByToken(TEST_OWN_RESTORE_DTO.getToken());
        verify(passwordEncoder).encode(TEST_OWN_RESTORE_DTO.getPassword());
        verify(ownSecurityRepo).findByUserId(2L);
        verify(userRepo).findById(2L);
        verify(applicationEventPublisher).publishEvent(any());
        verify(restorePasswordEmailRepo).delete(TEST_RESTORE_PASSWORD_EMAIL);
    }

    @Test
    void testUpdatePasswordUsingTokenThrowsNotFoundException() {
        when(restorePasswordEmailRepo.findByToken(TEST_OWN_RESTORE_DTO.getToken()))
            .thenReturn(empty());

        assertThrows(NotFoundException.class,
            () -> passwordRecoveryService.updatePasswordUsingToken(TEST_OWN_RESTORE_DTO));
    }

    @Test
    void testUpdatePasswordUsingTokenPasswordDoesNotMatch() {
        when(restorePasswordEmailRepo.findByToken(TEST_OWN_RESTORE_DTO.getToken()))
            .thenReturn(ofNullable(TEST_RESTORE_PASSWORD_EMAIL));

        assertThrows(BadRequestException.class,
            () -> passwordRecoveryService.updatePasswordUsingToken(TEST_OWN_RESTORE_DTO_WRONG));
    }

    @Test
    void testUpdatePasswordUsingExpiredToken() {
        when(restorePasswordEmailRepo.findByToken(TEST_OWN_RESTORE_DTO.getToken()))
            .thenReturn(ofNullable(TEST_RESTORE_PASSWORD_EMAIL_EXPIRED_TOKEN));

        assertThrows(UserActivationEmailTokenExpiredException.class,
            () -> passwordRecoveryService.updatePasswordUsingToken(TEST_OWN_RESTORE_DTO));
    }
}
