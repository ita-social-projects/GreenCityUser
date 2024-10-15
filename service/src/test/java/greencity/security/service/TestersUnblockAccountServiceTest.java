package greencity.security.service;

import greencity.dto.security.UnblockTestersAccountDto;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.UserRepo;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestersUnblockAccountServiceTest {
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private TestersUnblockAccountServiceImpl testersUnblockAccountService;

    @Test
    void shouldUnblockTestersAccount() {
        UnblockTestersAccountDto dto = new UnblockTestersAccountDto("m5VZl@example.com", "email");

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(User.builder()
            .email("email")
            .userStatus(UserStatus.BLOCKED)
            .build()));

        testersUnblockAccountService.unblockAccount(dto);

        verify(userRepo).findByEmail(anyString());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void shouldNotUnblockTestersAccount() {
        UnblockTestersAccountDto dto = new UnblockTestersAccountDto("test", "email");

        assertThrows(BadRequestException.class, () -> testersUnblockAccountService.unblockAccount(dto));
    }
}
