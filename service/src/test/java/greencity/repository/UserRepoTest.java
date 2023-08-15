package greencity.repository;

import greencity.ModelUtils;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static greencity.enums.EmailNotification.DISABLED;
import static greencity.enums.EmailNotification.IMMEDIATELY;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;

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
    void findNotDeactivatedByEmailTest() {
        when(userRepo.findNotDeactivatedByEmail("test@email.com")).thenReturn(Optional.of(ModelUtils.getUser()));
        User actual = userRepo.findNotDeactivatedByEmail("test@email.com").get();
        assertEquals(1L, actual.getId());
        verify(userRepo).findNotDeactivatedByEmail("test@email.com");
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
        assertEquals("test2@email.com", disabled.get(0).getEmail());
        verify(userRepo).findAllByEmailNotification(EmailNotification.DISABLED);
        verify(userRepo, times(1)).findAllByEmailNotification(EmailNotification.IMMEDIATELY);
    }

    @Test
    void countAllByUserStatusTest() {
        Long expected = 8L;
        when(userRepo.countAllByUserStatus(any())).thenReturn(expected);
        Long actual = userRepo.countAllByUserStatus(UserStatus.ACTIVATED);
        assertEquals(expected, actual);
        verify(userRepo).countAllByUserStatus(UserStatus.ACTIVATED);
    }

    @Test
    void getProfilePicturePathByUserIdTest() {
        String expected = "pathToPicture";
        when(userRepo.getProfilePicturePathByUserId(anyLong())).thenReturn(Optional.of(expected));
        String actual = userRepo.getProfilePicturePathByUserId(5L).get();
        assertEquals(expected, actual);
        verify(userRepo).getProfilePicturePathByUserId(5L);
    }

    @Test
    void getAllUserFriendsTest() {
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

        List<User> users = Arrays.asList(user, user2, user3, user4, user5);
        when(userRepo.getAllUserFriends(anyLong())).thenReturn(users);
        List<User> actual = userRepo.getAllUserFriends(1L);
        assertEquals(5, actual.size());
        assertEquals(1, actual.get(1).getId());
        verify(userRepo).getAllUserFriends(1L);
    }

    @Test
    void getAllUserFriendsPageTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<User> users = Arrays.asList(new User(), new User());
        Page<User> expectedPage = new PageImpl<>(users, pageable, users.size());
        when(userRepo.getAllUserFriends(anyLong(), any())).thenReturn(expectedPage);

        Page<User> actual = userRepo.getAllUserFriends(1L, pageable);
        assertEquals(2, actual.getContent().size());
        assertEquals(expectedPage.getContent(), actual.getContent());
        verify(userRepo).getAllUserFriends(1L, pageable);
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() {
        User user1 = ModelUtils.getUser();
        user1.setId(1L);
        User user2 = ModelUtils.getUser();
        user2.setId(2L);
        User user3 = ModelUtils.getUser();
        user3.setId(3L);

        List<User> friends = Arrays.asList(user1, user2, user3);

        when(userRepo.getSixFriendsWithTheHighestRating(anyLong())).thenReturn(friends);

        List<User> highestRatedFriends = userRepo.getSixFriendsWithTheHighestRating(1L);
        assertEquals(3, highestRatedFriends.size());
        assertTrue(highestRatedFriends.contains(user1));
        assertTrue(highestRatedFriends.contains(user2));
        assertTrue(highestRatedFriends.contains(user3));
        verify(userRepo).getSixFriendsWithTheHighestRating(1L);
    }

    @Test
    void deactivateSelectedUsersTest() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        doNothing().when(userRepo).deactivateSelectedUsers(ids);

        userRepo.deactivateSelectedUsers(ids);

        verify(userRepo).deactivateSelectedUsers(ids);

        User deactivatedUser1 = new User();
        deactivatedUser1.setUserStatus(UserStatus.DEACTIVATED);

        User deactivatedUser2 = new User();
        deactivatedUser2.setUserStatus(UserStatus.DEACTIVATED);

        User activatedUser3 = new User();
        activatedUser3.setUserStatus(UserStatus.ACTIVATED);

        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(deactivatedUser1));
        when(userRepo.findByEmail("test2@email.com")).thenReturn(Optional.of(deactivatedUser2));
        when(userRepo.findByEmail("test3@email.com")).thenReturn(Optional.of(activatedUser3));

        assertEquals(UserStatus.DEACTIVATED.toString(),
            userRepo.findByEmail("test@email.com").get().getUserStatus().toString());
        assertEquals(UserStatus.DEACTIVATED.toString(),
            userRepo.findByEmail("test2@email.com").get().getUserStatus().toString());
        assertEquals(UserStatus.ACTIVATED.toString(),
            userRepo.findByEmail("test3@email.com").get().getUserStatus().toString());
        verify(userRepo).deactivateSelectedUsers(ids);
        verify(userRepo, times(3)).findByEmail(anyString());
    }

    @Test
    void searchByTest() {
        Pageable pageable = PageRequest.of(0, 3);
        User user3 = ModelUtils.getUser();
        user3.setId(3L);
        List<User> users = Arrays.asList(user3);
        Page<User> expectedPage = new PageImpl<>(users, pageable, users.size());

        when(userRepo.searchBy(any(Pageable.class), anyString())).thenReturn(expectedPage);

        Page<User> actualPage = userRepo.searchBy(pageable, "test3@email.com");

        List<Long> actualIds = actualPage.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expectedPage.getContent().stream().map(User::getId)
            .collect(Collectors.toList());

        assertEquals(1, expectedPage.getContent().size());
        assertEquals(expectedIds, actualIds);
        verify(userRepo).searchBy(pageable, "test3@email.com");
    }

    @Test
    void findAllUsersCitiesTest() {
        List<String> expectedCities = Arrays.asList("New York", "LA", "Chicago", "Miami",
            "Dallas", "Toronto", "Montreal", "Montreal", "Liverpool");

        when(userRepo.findAllUsersCities()).thenReturn(expectedCities);

        List<String> actualCities = userRepo.findAllUsersCities();
        assertEquals(expectedCities, actualCities);
        assertEquals(9, actualCities.size());
        verify(userRepo).findAllUsersCities();
    }

    @Test
    void findUserForAchievementTest() {
        User expectedUser = ModelUtils.getUser();
        expectedUser.setId(1L);

        when(userRepo.findUserForAchievement(anyLong())).thenReturn(Optional.of(expectedUser));

        User actualUser = userRepo.findUserForAchievement(1L).orElse(null);

        assertEquals(expectedUser, actualUser);
        verify(userRepo).findUserForAchievement(1L);
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
    void countOfMutualFriendsTest() {
        Integer expected = 2;

        when(userRepo.countOfMutualFriends(anyLong())).thenReturn(expected);

        Integer actualFriends = userRepo.countOfMutualFriends(1L);

        assertEquals(expected, actualFriends);
        verify(userRepo).countOfMutualFriends(1L);
    }

    @Test
    void updateUserLastActivityTimeTest() {
        LocalDateTime expectedTime = LocalDateTime.of(2020, 9, 29, 0, 0, 0);
        User user3 = ModelUtils.getUser();
        user3.setId(3L);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of((user3)));

        userRepo.updateUserLastActivityTime(3L, expectedTime);

        User user = userRepo.findByEmail("test3@email.com").get();

        assertEquals(expectedTime, user.getLastActivityTime());
        verify(userRepo).findByEmail("test3@email.com");
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