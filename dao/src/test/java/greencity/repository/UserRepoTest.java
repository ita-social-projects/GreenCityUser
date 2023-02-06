package greencity.repository;

import greencity.dto.user.UsersFriendDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static greencity.enums.EmailNotification.DISABLED;
import static greencity.enums.EmailNotification.IMMEDIATELY;
import static greencity.enums.UserStatus.ACTIVATED;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql("classpath:sql/user_repo.sql")
class UserRepoTest {
    @Autowired
    UserRepo userRepo;

    @Test
    void findByEmailTest() {
        Long expected = 1L;
        User actual = userRepo.findByEmail("test@email.com").get();
        assertEquals(expected, actual.getId());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 3);
        User user1 = userRepo.findByEmail("test@email.com").get();
        User user2 = userRepo.findByEmail("test2@email.com").get();
        User user3 = userRepo.findByEmail("test3@email.com").get();
        List<User> users = Arrays.asList(user1, user2, user3);
        Page<User> actual = new PageImpl<>(users, pageable, users.size());
        Page<User> expected = userRepo.findAll(pageable);
        List<Long> actualIds = actual.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        assertEquals(3, expected.getContent().size());
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void findAllByIdsTest() {
        // given
        List<Long> ids = List.of(1L, 2L, 5L);

        // when
        List<User> allByIds = userRepo.findAllByIdIn(ids);

        // then
        assertEquals(3, allByIds.size());
        assertTrue(allByIds.contains(userRepo.findById(1L).get()));
        assertTrue(allByIds.contains(userRepo.findById(5L).get()));
        assertFalse(allByIds.contains(userRepo.findById(4L).get()));
    }

    @Test
    void findIdByEmailTest() {
        Long expected = 2L;
        Long actual = userRepo.findIdByEmail("test2@email.com").get();
        assertEquals(expected, actual);
    }

    @Test
    void findUuidByEmail() {
        String expected = "444e66e8-8daa-4cb0-8269-a8d856e7dd15";
        String actual = userRepo.findUuidByEmail("test3@email.com").get();
        assertEquals(expected, actual);
    }

    @Test
    void checkIfNotDeactivatedTest() {
        String oneIfNotDeactivated = userRepo.checkIfNotDeactivated("test@email.com").get();
        assertEquals("1", oneIfNotDeactivated);
    }

    @Test
    void findNotDeactivatedByEmailTest() {
        User actual = userRepo.findNotDeactivatedByEmail("test@email.com").get();
        assertEquals(1L, actual.getId());
    }

    @Test
    void findAllByEmailNotificationTest() {
        List<User> disabled = userRepo.findAllByEmailNotification(DISABLED);
        List<User> immediately = userRepo.findAllByEmailNotification(IMMEDIATELY);
        assertEquals(7, disabled.size());
        assertEquals(2L, disabled.get(1).getId());
        assertEquals(2, immediately.size());
        assertEquals("test4@email.com", immediately.get(0).getEmail());
    }

    @Test
    void updateUserRefreshTokenTest() {
        String newToken = "NewToken";
        userRepo.updateUserRefreshToken(newToken, 1L);
        User user = userRepo.findByEmail("test@email.com").get();
        assertEquals(newToken, user.getRefreshTokenKey());
    }

    @Test
    void countAllByUserStatusTest() {
        Long expected = 8L;
        Long actual = userRepo.countAllByUserStatus(ACTIVATED);
        assertEquals(expected, actual);
    }

    @Test
    void getProfilePicturePathByUserIdTest() {
        String expected = "pathToPicture";
        String actual = userRepo.getProfilePicturePathByUserId(5L).get();
        assertEquals(expected, actual);
    }

    @Test
    void getAllUserFriendsTest() {
        List<User> actual = userRepo.getAllUserFriends(1L);
        assertEquals(7, actual.size());
        assertEquals(3, actual.get(1).getId());
    }

    @Test
    void getAllUserFriendsPageTest() {
        Pageable pageable = PageRequest.of(0, 2);
        User user2 = userRepo.findByEmail("test2@email.com").get();
        User user3 = userRepo.findByEmail("test3@email.com").get();
        List<User> users = Arrays.asList(user2, user3);
        Page<User> actual = new PageImpl<>(users, pageable, users.size());
        Page<User> expected = userRepo.getAllUserFriends(1L, pageable);
        List<Long> actualIds = actual.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        assertEquals(2, expected.getContent().size());
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void getAllUserFriendRequestsPageTest() {
        Pageable pageable = PageRequest.of(0, 2);
        User user = userRepo.findByEmail("test5@email.com").get();
        List<User> users = Arrays.asList(user);
        Page<User> actual = new PageImpl<>(users, pageable, users.size());
        Page<User> expected = userRepo.getAllUserFriendRequests(4L, pageable);
        List<Long> actualIds = actual.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        assertEquals(1, expected.getContent().size());
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void getAllUserFriendRequestsTest() {
        List<User> users = userRepo.getAllUserFriendRequests(4L);
        assertEquals(1, users.size());
        assertEquals(5, users.get(0).getId());
    }

    @Test
    void deleteUserFriendByIdTest() {
        userRepo.deleteUserFriendById(1L, 2L);
        List<User> users = userRepo.getAllUserFriends(1L);
        assertEquals(6, users.size());
        assertEquals(3, users.get(0).getId());
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() {
        List<User> friends = userRepo.getSixFriendsWithTheHighestRating(1L);
        User user2 = userRepo.findByEmail("test2@email.com").get();
        User user4 = userRepo.findByEmail("test4@email.com").get();
        User user6 = userRepo.findByEmail("test6@email.com").get();
        assertEquals(6, friends.size());
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user4));
        assertFalse(friends.contains(user6));
    }

    @Test
    void getAllUserFriendsCountTest() {
        Integer friends = userRepo.getAllUserFriendsCount(1L);
        assertEquals(7, friends);
    }

    @Test
    void searchByTest() {
        Pageable pageable = PageRequest.of(0, 3);
        User user3 = userRepo.findByEmail("test3@email.com").get();
        List<User> users = Arrays.asList(user3);
        Page<User> actual = new PageImpl<>(users, pageable, users.size());
        Page<User> expected = userRepo.searchBy(pageable, "test3@email.com");
        List<Long> actualIds = actual.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        List<Long> expectedIds = expected.getContent().stream().map(User::getId)
            .collect(Collectors.toList());
        assertEquals(1, expected.getContent().size());
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void findAllUsersCitiesTest() {
        List<String> actual = Arrays.asList("New York", "LA", "Chicago", "Miami",
            "Dallas", "Toronto", "Montreal", "Montreal", "Liverpool");
        List<String> expected = userRepo.findAllUsersCities();
        assertEquals(expected, actual);
        assertEquals(9, expected.size());
    }

    @Test
    void findUserForAchievementTest() {
        User actual = userRepo.findByEmail("test@email.com").get();
        User expected = userRepo.findUserForAchievement(1L).get();
        assertEquals(expected, actual);
    }

    @Test
    void findUserByUuidTest() {
        String uuid = "1488";
        User actual = userRepo.findByEmail("test8@email.com").get();
        User expected = userRepo.findUserByUuid(uuid).get();
        assertEquals(expected, actual);
    }

    @Test
    void findUsersByNameTest() {
        Pageable pageable = PageRequest.of(0, 3);
        User user = userRepo.findByEmail("test2@email.com").get();
        Page<User> expected = userRepo.findUsersByName("SuperTest2", pageable, 3L);
        assertEquals(user, expected.getContent().get(0));
    }

    @Test
    void countOfMutualFriendsTest() {
        Integer expected = 2;
        Integer friends = userRepo.countOfMutualFriends(1L);
        assertEquals(expected, friends);
    }

    @Test
    void addNewFriendTest() {
        User user3 = userRepo.findByEmail("test3@email.com").get();
        User user7 = userRepo.findByEmail("test7@email.com").get();
        User user8 = userRepo.findByEmail("test8@email.com").get();
        userRepo.addNewFriend(user3.getId(), user7.getId());
        userRepo.addNewFriend(user3.getId(), user8.getId());
        List<User> friends = userRepo.getAllUserFriendsWithoutStatus(user3.getId());
        assertEquals(3, friends.size());
    }

    @Test
    void acceptFriendRequestTest() {
        userRepo.acceptFriendRequest(4L, 5L);
        List<User> friends = userRepo.getAllUserFriends(4L);
        assertEquals(2, friends.size());
    }

    @Test
    void declineFriendRequestTest() {
        userRepo.declineFriendRequest(4L, 1L);
        List<User> friends = userRepo.getAllUserFriends(4L);
        assertEquals(0, friends.size());
    }

    @Test
    void updateUserLastActivityTimeTest() {
        LocalDateTime expected = LocalDateTime.of(2021, 5, 31, 22, 0, 0);
        userRepo.updateUserLastActivityTime(3L, expected);
        User user = userRepo.findByEmail("test3@email.com").get();
        assertEquals(expected, user.getLastActivityTime());
    }

    @Test
    void findLastActivityTimeByIdTest() {
        Timestamp expected = Timestamp.valueOf(LocalDateTime.of(2020, 9, 29, 0, 0, 0));
        Timestamp actual = userRepo.findLastActivityTimeById(8L).get();
        assertEquals(expected, actual);
    }

    @Test
    void findAnyRecommendedFriendsTest() {
        List<UsersFriendDto> friendDtos = userRepo.findAnyRecommendedFriends(1L);
        assertEquals(1, friendDtos.size());
    }
}