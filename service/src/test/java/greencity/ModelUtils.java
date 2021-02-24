package greencity;

import greencity.constant.AppConstant;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.*;
import greencity.dto.useraction.UserActionVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.Achievement;
import greencity.entity.AchievementCategory;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.UserAchievement;
import greencity.entity.VerifyEmail;
import greencity.entity.localization.AchievementTranslation;
import greencity.enums.AchievementStatus;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public class ModelUtils {
    public static UsersFriendDto usersFriendDto = new UsersFriendDto() {
        @Override
        public Long getId() {
            return 1L;
        }

        @Override
        public String getName() {
            return TestConst.NAME;
        }

        @Override
        public String getCity() {
            return "Test";
        }

        @Override
        public Double getRating() {
            return 20.0;
        }

        @Override
        public String getProfilePicture() {
            return "profile";
        }
    };

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static RecommendedFriendDto getRecommendedFriendDto() {
        return new RecommendedFriendDto(1L, TestConst.NAME, "profile");
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .verifyEmail(new VerifyEmailVO())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static UserVO getUserVOWithData() {
        return UserVO.builder()
            .id(13L)
            .name("user")
            .email("namesurname1995@gmail.com")
            .role(Role.ROLE_USER)
            .userCredo("save the world")
            .firstName("name")
            .emailNotification(EmailNotification.MONTHLY)
            .userStatus(UserStatus.ACTIVATED)
            .lastVisit(LocalDateTime.of(2020, 12, 11, 13, 33))
            .rating(13.4)
            .verifyEmail(VerifyEmailVO.builder()
                .id(32L)
                .user(UserVO.builder()
                    .id(13L)
                    .name("user")
                    .build())
                .expiryDate(LocalDateTime.of(2021, 7, 7, 7, 7))
                .token("toooookkkeeeeen42324532542")
                .build())
            .userFriends(Collections.singletonList(
                UserVO.builder()
                    .id(75L)
                    .name("Andrew")
                    .build()))
            .refreshTokenKey("refreshtoooookkkeeeeen42324532542")
            .ownSecurity(null)
            .dateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47))
            .city("Lviv")
            .showShoppingList(true)
            .showEcoPlace(true)
            .showLocation(true)
            .ownSecurity(OwnSecurityVO.builder()
                .id(1L)
                .password("password")
                .user(UserVO.builder()
                    .id(13L)
                    .build())
                .build())
            .lastActivityTime(LocalDateTime.of(2020, 12, 11, 13, 30))
            .userAchievements(List.of(
                UserAchievementVO.builder()
                    .id(47L)
                    .achievementStatus(AchievementStatus.ACTIVE)
                    .user(UserVO.builder()
                        .id(13L)
                        .build())
                    .achievement(AchievementVO.builder()
                        .id(56L)
                        .build())
                    .build(),
                UserAchievementVO.builder()
                    .id(39L)
                    .achievementStatus(AchievementStatus.INACTIVE)
                    .user(UserVO.builder()
                        .id(13L)
                        .build())
                    .achievement(AchievementVO.builder()
                        .id(14L)
                        .build())
                    .build()))
            .userActions(Collections.singletonList(UserActionVO.builder()
                .id(13L)
                .achievementCategory(AchievementCategoryVO.builder()
                    .id(1L)
                    .build())
                .count(0)
                .user(UserVO.builder()
                    .id(13L)
                    .build())
                .build()))
            .build();
    }

    public static Language getLanguage() {
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE);
    }

    public static UserProfilePictureDto getUserProfilePictureDto() {
        return new UserProfilePictureDto(1L, "name", "image");
    }

    public static Achievement getAchievement() {
        return new Achievement(1L, Collections.singletonList(getAchievementTranslation()), Collections.emptyList(),
            new AchievementCategory(), 1);
    }

    public static AchievementVO getAchievementVO() {
        return new AchievementVO(1L, Collections.emptyList(), Collections.emptyList(), new AchievementCategoryVO(), 1);
    }

    public static AchievementTranslation getAchievementTranslation() {
        return new AchievementTranslation(1L, getLanguage(), "Title", "Description", "Message", null);
    }

    public static UserAchievement getUserAchievement() {
        return new UserAchievement(1L, getUser(), getAchievement(), AchievementStatus.ACTIVE, false);
    }

    public static EcoNewsAuthorDto getEcoNewsAuthorDto() {
        return EcoNewsAuthorDto.builder()
            .id(1L)
            .name("taras")
            .build();
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return AddEcoNewsDtoResponse.builder()
            .id(1L)
            .title("title")
            .text("texttexttexttext")
            .ecoNewsAuthorDto(getEcoNewsAuthorDto())
            .creationDate(ZonedDateTime.now())
            .imagePath("/imagePath")
            .source("source")
            .tags(Collections.singletonList("tag"))
            .build();
    }
}
