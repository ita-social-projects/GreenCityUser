package greencity;

import greencity.dto.CoordinatesDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserLocationDto;
import greencity.dto.user.UserNotificationPreferenceDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOShort;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import jakarta.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

public class ModelUtils {

    public static URL getUrl() throws MalformedURLException {
        return URI.create(TestConst.SITE).toURL();
    }

    public static AchievementVO getAchievementVO() {
        return new AchievementVO(1L,
            "ACQUIRED_HABIT_14_DAYS",
            "Набуття звички протягом 14 днів",
            "Acquired habit 14 days",
            new AchievementCategoryVO(1L, "name", List.of(), List.of()),
            1);
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(TestConst.USER_ID)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .build();
    }

    public static UserVOShort getUserVOShort() {
        return UserVOShort.builder()
            .id(TestConst.USER_ID)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .build();
    }

    public static UserAchievementVO getUserAchievementVO() {
        return new UserAchievementVO(1L, getUserVO(), getAchievementVO(), false);
    }

    public static UserCityDto getUserCityDto() {
        return new UserCityDto(
            1L,
            "cityEn", "cityUk",
            0., 0.);
    }

    public static UserProfileDtoRequest getUserProfileDtoRequest() {
        return new UserProfileDtoRequest(
            "name",
            List.of("social network 1", "social network 2"),
            ProfilePrivacyPolicy.FRIENDS_ONLY,
            ProfilePrivacyPolicy.PRIVATE,
            ProfilePrivacyPolicy.PUBLIC,
            new CoordinatesDto(),
            Set.of(new UserNotificationPreferenceDto()));
    }

    public static CreateGreenCityUserDto getCreateGreenCityUserDto() {
        return new CreateGreenCityUserDto(
            TestConst.USER_ID,
            TestConst.EMAIL,
            TestConst.NAME,
            TestConst.PROFILE_PICTURE_PATH);
    }

    public static GreenCityUserProfileDtoResponse getGreenCityUserProfileDtoResponse() {
        return new GreenCityUserProfileDtoResponse(
            TestConst.USER_ID,
            TestConst.EMAIL,
            TestConst.PROFILE_PICTURE_PATH,
            2.,
            new UserLocationDto());
    }

    public static UbsProfileCreationDto getUbsProfileCreationDto() {
        return new UbsProfileCreationDto(
            TestConst.UUID,
            TestConst.EMAIL,
            TestConst.NAME);
    }

    public static ConstraintValidatorContext.ConstraintViolationBuilder getConstraintViolationBuilder() {
        return new ConstraintValidatorContext.ConstraintViolationBuilder() {
            @Override
            public NodeBuilderDefinedContext addNode(String name) {
                return null;
            }

            @Override
            public NodeBuilderCustomizableContext addPropertyNode(String name) {
                return null;
            }

            @Override
            public LeafNodeBuilderCustomizableContext addBeanNode() {
                return null;
            }

            @Override
            public ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String name,
                Class<?> containerType,
                Integer typeArgumentIndex) {
                return null;
            }

            @Override
            public NodeBuilderDefinedContext addParameterNode(int index) {
                return null;
            }

            @Override
            public ConstraintValidatorContext addConstraintViolation() {
                return null;
            }
        };
    }
}
