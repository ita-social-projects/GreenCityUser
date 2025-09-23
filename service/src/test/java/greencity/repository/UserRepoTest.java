package greencity.repository;

import greencity.ModelUtils;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import static greencity.enums.EmailNotification.DISABLED;
import static greencity.enums.EmailNotification.IMMEDIATELY;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserRepoTest {
    @Mock
    UserRepo userRepo;

    @Test
    void findByEmailTest() {
        Long expected = 1L;
        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(ModelUtils.getUser()));
        User actual = userRepo.findByEmail("test@email.com").get();
        assertEquals(expected, actual.getId());
        verify(userRepo, times(1)).findByEmail("test@email.com");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 3);
        List<User> mockUsers = Arrays.asList(ModelUtils.getUser(), ModelUtils.getUser(), ModelUtils.getUser());

        Page<User> expectedPage = new PageImpl<>(mockUsers, pageable, mockUsers.size());

        when(userRepo.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<User> actualPage = userRepo.findAll(pageable);

        List<Long> actualIds = actualPage.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expectedPage.getContent().stream().map(User::getId)
            .collect(Collectors.toList());

        assertEquals(3, expectedPage.getContent().size());
        assertEquals(expectedIds, actualIds);
        verify(userRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void findIdByEmailTest() {
        Long expected = 1L;
        when(userRepo.findIdByEmail("test@email.com")).thenReturn(Optional.of(ModelUtils.getUser().getId()));
        Long actual = userRepo.findIdByEmail("test@email.com").get();
        assertEquals(expected, actual);
        verify(userRepo, times(1)).findIdByEmail("test@email.com");
    }

    @Test
    void findUuidByEmail() {
        String expected = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        when(userRepo.findUuidByEmail("test@email.com")).thenReturn(Optional.of(ModelUtils.getUser().getUuid()));
        String actual = userRepo.findUuidByEmail("test@email.com").get();
        assertEquals(expected, actual);
        verify(userRepo, times(1)).findUuidByEmail("test@email.com");
    }

    @Test
    void findAllByEmailNotificationTest() {
        User user = ModelUtils.getUser();
        user.setEmailNotification(EmailNotification.MONTHLY);
        User user2 = ModelUtils.getUser();
        user2.setEmailNotification(EmailNotification.DISABLED);
        user2.setEmail("test2@email.com");
        User user3 = ModelUtils.getUser();
        user3.setEmailNotification(EmailNotification.MONTHLY);
        User user4 = ModelUtils.getUser();
        user4.setEmailNotification(EmailNotification.IMMEDIATELY);
        User user5 = ModelUtils.getUser();
        user5.setEmailNotification(EmailNotification.IMMEDIATELY);

        when(userRepo.findAllByEmailNotification(DISABLED)).thenReturn(Arrays.asList(user2));
        when(userRepo.findAllByEmailNotification(IMMEDIATELY)).thenReturn(Arrays.asList(user4, user5));

        List<User> disabled = userRepo.findAllByEmailNotification(DISABLED);
        List<User> immediately = userRepo.findAllByEmailNotification(IMMEDIATELY);
        assertEquals(1, disabled.size());
        assertEquals(1L, immediately.get(1).getId());
        assertEquals(2, immediately.size());
        assertEquals("test2@email.com", disabled.getFirst().getEmail());
        verify(userRepo).findAllByEmailNotification(EmailNotification.DISABLED);
        verify(userRepo, times(1)).findAllByEmailNotification(EmailNotification.IMMEDIATELY);
    }

    @Test
    void findUserByUuidTest() {
        String uuid = "1488";
        User expectedUser = ModelUtils.getUser();
        expectedUser.setId(8L);

        when(userRepo.findUserByUuid(anyString())).thenReturn(Optional.of(expectedUser));

        User actualUser = userRepo.findUserByUuid(uuid).get();

        assertEquals(expectedUser, actualUser);
        verify(userRepo).findUserByUuid(uuid);
    }

    @Test
    void findLastActivityTimeByIdTest() {
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.of(2020, 9, 29, 0, 0, 0));

        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(expectedTimestamp));

        Timestamp actualTimestamp = userRepo.findLastActivityTimeById(8L).get();

        assertEquals(expectedTimestamp, actualTimestamp);
        verify(userRepo).findLastActivityTimeById(8L);
    }

}