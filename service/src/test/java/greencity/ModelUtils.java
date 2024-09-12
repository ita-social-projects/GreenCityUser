package greencity;

import com.google.maps.model.*;
import greencity.constant.AppConstant;
import greencity.dto.CoordinatesDto;
import greencity.dto.UbsCustomerDto;
import greencity.dto.achievement.AchievementVO;
import greencity.dto.achievement.UserAchievementVO;
import greencity.dto.achievementcategory.AchievementCategoryVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.position.PositionAuthoritiesDto;
import greencity.dto.position.PositionDto;
import greencity.dto.ubs.UbsProfileCreationDto;
import greencity.dto.user.*;
import greencity.dto.useraction.UserActionVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.dto.violation.UserViolationMailDto;
import greencity.entity.*;
import greencity.enums.EmailNotification;
import greencity.enums.EmailPreference;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.security.dto.ownsecurity.EmployeeSignUpDto;
import greencity.security.dto.ownsecurity.OwnRestoreDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ModelUtils {
    public static final User TEST_USER = createUser();
    public static final User TEST_ADMIN = createAdmin();
    public static final UserVO TEST_USER_VO = createTestUserVO();
    public static final OwnSecurity TEST_OWN_SECURITY = createOwnSecurity();
    public static final RestorePasswordEmail TEST_RESTORE_PASSWORD_EMAIL = createRestorePasswordEmail();
    public static final RestorePasswordEmail TEST_RESTORE_PASSWORD_EMAIL_EXPIRED_TOKEN =
        createRestorePasswordEmailExpiredToken();
    public static final OwnRestoreDto TEST_OWN_RESTORE_DTO = createOwnRestoreDto();
    public static final OwnRestoreDto TEST_OWN_RESTORE_DTO_WRONG = createOwnRestoreDtoWrong();
    public static final UserProfileStatisticsDto USER_PROFILE_STATISTICS_DTO = createUserProfileStatisticsDto();
    public static final UserManagementDto CREATE_USER_MANAGER_DTO = createUserManagerDto();
    public static final List<UserAllFriendsDto> CREATE_USER_ALL_FRIENDS_DTO = createUserAllFriendsDto();
    public static final String TEST_EMAIL = "taras@gmail.com";

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.DEACTIVATED)
            .lastActivityTime(LocalDateTime.of(2020, 9, 29, 0, 0, 0))
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(LocalDateTime.now())
            .uuid("444e66e8-8daa-4cb0-8269-a8d856e7dd15")
            .build();
    }

    public static User getUserWithUserLocation() {
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.DEACTIVATED)
            .lastActivityTime(LocalDateTime.of(2020, 9, 29, 0, 0, 0))
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(LocalDateTime.now())
            .userLocation(new UserLocation(1L, "Lviv", "Львів", "Lvivska",
                "Львівська", "Ukraine", "Україна", 20.000000,
                20.000000, new ArrayList<>()))
            .language(new Language(1L, "en", null))
            .build();
    }

    private static UserManagementDto createUserManagerDto() {
        return UserManagementDto.builder()
            .id(1L)
            .name("Martin")
            .email("martin@gmail.com")
            .userCredo("credo")
            .role(Role.ROLE_ADMIN)
            .userStatus(UserStatus.ACTIVATED).build();
    }

    public static UserManagementUpdateDto getUserManagementUpdateDto() {
        return UserManagementUpdateDto.builder()
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .email(TestConst.EMAIL)
            .userCredo(TestConst.CREDO)
            .build();
    }

    private static UserProfileStatisticsDto createUserProfileStatisticsDto() {
        return UserProfileStatisticsDto.builder()
            .amountHabitsInProgress(TestConst.SIMPLE_LONG_NUMBER)
            .amountHabitsAcquired(TestConst.SIMPLE_LONG_NUMBER)
            .amountPublishedNews(TestConst.SIMPLE_LONG_NUMBER)
            .amountOrganizedAndAttendedEvents(TestConst.SIMPLE_LONG_TWO_NUMBER)
            .build();
    }

    private static List<UserAllFriendsDto> createUserAllFriendsDto() {
        List<UserAllFriendsDto> list = new ArrayList<>();

        list.add(UserAllFriendsDto.builder()
            .id(1L)
            .name("Martin")
            .city("New-York")
            .rating(30.00)
            .mutualFriends(11L)
            .profilePicturePath("Picture")
            .build());
        list.add(UserAllFriendsDto.builder()
            .id(1L)
            .name("Martin")
            .city("New-York")
            .rating(30.00)
            .mutualFriends(11L)
            .profilePicturePath("Picture")
            .build());
        list.add(UserAllFriendsDto.builder()
            .id(1L)
            .name("Martin")
            .city("New-York")
            .rating(30.00)
            .mutualFriends(11L)
            .profilePicturePath("Picture")
            .build());
        return list;
    }

    public static List<Authority> authorities() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
            .id(1L)
            .email("taras@gmail.com")
            .role(Role.ROLE_UBS_EMPLOYEE)
            .build());
        List<Authority> authorities = new ArrayList<>();
        authorities.add(Authority.builder()
            .id(1L)
            .name("test1")
            .employees(users)
            .build());
        authorities.add(Authority.builder()
            .id(2L)
            .name("test2")
            .employees(users)
            .build());
        return authorities;
    }

    public static UserEmployeeAuthorityDto getUserEmployeeAuthorityDto() {
        return UserEmployeeAuthorityDto.builder()
            .employeeEmail("taras@gmail.com")
            .authorities(List.of("test1"))
            .build();
    }

    public static List<Position> getPositions() {
        return List.of(Position.builder()
            .id(1L)
            .name("Супер адмін")
            .nameEn("Super admin")
            .build());
    }

    public static Authority getAuthority() {
        List<User> list = new ArrayList<>();
        list.add(createUser());
        return Authority.builder()
            .id(1L)
            .name("test1")
            .employees(list)
            .build();
    }

    public static User getUserWithSocialNetworks() {
        List<SocialNetwork> socialNetwork = new ArrayList<>();
        socialNetwork.add(SocialNetwork.builder()
            .id(1L)
            .url("https://www.facebook.com")
            .build());
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .language(ModelUtils.getLanguage())
            .socialNetworks(socialNetwork)
            .lastActivityTime(LocalDateTime.now())
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(LocalDateTime.now())
            .userLocation(null)
            .build();
    }

    public static User getUserWithUbsRole() {
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_UBS_EMPLOYEE)
            .lastActivityTime(LocalDateTime.now())
            .verifyEmail(new VerifyEmail())
            .restorePasswordEmail(new RestorePasswordEmail())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .lastActivityTime(LocalDateTime.now())
            .verifyEmail(new VerifyEmailVO())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    }

    public static UbsCustomerDto getUbsCustomerDtoWithData() {
        return UbsCustomerDto.builder()
            .email("nazar.struk@gmail.com")
            .build();
    }

    public static UserVO getUserVOWithData() {
        return UserVO.builder()
            .id(13L)
            .name("user")
            .email("namesurname1995@gmail.com")
            .role(Role.ROLE_USER)
            .userCredo("save the world")
            .emailNotification(EmailNotification.MONTHLY)
            .userStatus(UserStatus.ACTIVATED)
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
            .userLocationDto(
                new UserLocationDto(1L, "Lviv", "Львів", "Lvivska", "Львівська",
                    "Ukraine", "Україна", 20.000000,
                    20.000000))
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
                    .user(UserVO.builder()
                        .id(13L)
                        .build())
                    .achievement(AchievementVO.builder()
                        .id(56L)
                        .build())
                    .build(),
                UserAchievementVO.builder()
                    .id(39L)
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
            .languageVO(LanguageVO.builder()
                .id(1L)
                .code("ua")
                .build())
            .build();
    }

    public static UserProfileDtoRequest getUserProfileDtoRequest() {
        return UserProfileDtoRequest.builder()
            .name("Name")
            .userCredo("userCredo")
            .socialNetworks(List.of(
                "https://www.facebook.com",
                "https://www.instagram.com",
                "https://www.youtube.com",
                "https://www.gmail.com",
                "https://www.google.com"))
            .coordinates(new CoordinatesDto(1.0d, 1.0d))
            .showLocation(true)
            .showEcoPlace(true)
            .showShoppingList(true)
            .emailPreferences(Set.of(EmailPreference.LIKES, EmailPreference.COMMENTS))
            .build();
    }

    public static Language getLanguage() {
        return Language.builder().id(1L).code(AppConstant.DEFAULT_LANGUAGE_CODE).build();
    }

    public static UserProfilePictureDto getUserProfilePictureDto() {
        return new UserProfilePictureDto(1L, "name", "image");
    }

    public static Achievement getAchievement() {
        return new Achievement(1L, "ACQUIRED_HABIT_14_DAYS", "Набуття звички протягом 14 днів",
            "Acquired habit 14 days", Collections.emptyList(),
            new AchievementCategory(), 1);
    }

    public static AchievementVO getAchievementVO() {
        return new AchievementVO(1L, "ACQUIRED_HABIT_14_DAYS", "Набуття звички протягом 14 днів",
            "Acquired habit 14 days", new AchievementCategoryVO(), 1);
    }

    public static UserAchievement getUserAchievement() {
        return new UserAchievement(1L, getUser(), getAchievement(), false);
    }

    public static UserViolationMailDto getUserViolationMailDto() {
        return UserViolationMailDto.builder()
            .email("string@gmail.com")
            .name("string")
            .language("en")
            .violationDescription("String Description")
            .build();
    }

    public static UserAdminRegistrationDto getUserAdminRegistrationDto() {
        return UserAdminRegistrationDto.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.BLOCKED)
            .languageCode("en")
            .dateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47))
            .build();
    }

    private static RestorePasswordEmail createRestorePasswordEmailExpiredToken() {
        return RestorePasswordEmail.builder()
            .token("test")
            .id(1L)
            .user(TEST_USER)
            .expiryDate(LocalDateTime.now().minusDays(1L))
            .build();
    }

    private static OwnRestoreDto createOwnRestoreDtoWrong() {
        return OwnRestoreDto.builder()
            .token("test")
            .password("TestPassword&1")
            .confirmPassword("TestPassword&2")
            .build();
    }

    private static OwnSecurity createOwnSecurity() {
        return OwnSecurity.builder()
            .user(TEST_USER)
            .build();
    }

    private static User createUser() {
        return User.builder()
            .id(2L)
            .email("test@mail.com")
            .userStatus(UserStatus.CREATED)
            .role(Role.ROLE_USER)
            .rating(100D)
            .build();
    }

    public static User createEmployee() {
        return User.builder()
            .id(1L)
            .email("taras@gmail.com")
            .authorities(authorities())
            .role(Role.ROLE_UBS_EMPLOYEE)
            .positions(List.of(Position.builder()
                .id(1L)
                .name("Супер адмін")
                .nameEn("Super admin")
                .build()))
            .build();
    }

    public static User createAdmin() {
        return User.builder()
            .id(2L)
            .email("test@mail.com")
            .userStatus(UserStatus.CREATED)
            .role(Role.ROLE_ADMIN)
            .authorities(authorities())
            .build();
    }

    private static OwnRestoreDto createOwnRestoreDto() {
        return OwnRestoreDto.builder()
            .token("test")
            .password("TestPassword&1")
            .confirmPassword("TestPassword&1")
            .build();
    }

    private static RestorePasswordEmail createRestorePasswordEmail() {
        return RestorePasswordEmail.builder()
            .token("test")
            .id(1L)
            .user(TEST_USER)
            .expiryDate(LocalDateTime.now().plusDays(1L))
            .build();
    }

    private static UserVO createTestUserVO() {
        return UserVO.builder()
            .id(2L)
            .email("test@mail.com")
            .userStatus(UserStatus.CREATED)
            .role(Role.ROLE_ADMIN)
            .build();
    }

    public static EmployeeSignUpDto getEmployeeSignUpDto_UA() {
        return EmployeeSignUpDto.builder()
            .name("Taras")
            .email("test@mail.com")
            .positions(List.of(PositionDto.builder()
                .id(1L)
                .name("тест")
                .build()))
            .isUbs(true)
            .build();
    }

    public static EmployeeSignUpDto getEmployeeSignUpDto() {
        return EmployeeSignUpDto.builder()
            .name("Taras")
            .email("test@mail.com")
            .positions(List.of(PositionDto.builder()
                .id(1L)
                .name("тест")
                .nameEn("test")
                .build()))
            .isUbs(true)
            .build();
    }

    public static EmployeeSignUpDto getEmployeeSignUpDto_EN() {
        return EmployeeSignUpDto.builder()
            .name("Taras")
            .email("test@mail.com")
            .positions(List.of(PositionDto.builder()
                .id(1L)
                .nameEn("test")
                .build()))
            .isUbs(true)
            .build();
    }

    public static OwnSignUpDto getOwnSignUpDto() {
        return OwnSignUpDto.builder()
            .name("Name")
            .email("test@mail.com")
            .password("Test@123")
            .isUbs(true)
            .build();
    }

    public static UbsProfileCreationDto getUbsProfileCreationDto() {
        return UbsProfileCreationDto.builder()
            .name("UbsProfile")
            .email("ubsuser@mail.com")
            .uuid("f81d4fae-7dec-11d0-a765-00a0c91e6bf6")
            .build();
    }

    public static PositionAuthoritiesDto getPositionAuthoritiesDto() {
        return PositionAuthoritiesDto.builder()
            .positionId(List.of(1L))
            .authorities(List.of("Auth"))
            .build();
    }

    public static User getEmployeeWithPositionsAndRelatedAuthorities() {
        return User.builder()
            .positions(List.of(Position.builder()
                .id(1L).name("Адмін")
                .nameEn("Admin")
                .build()))
            .authorities(List.of(Authority.builder()
                .name("Auth")
                .build()))
            .build();
    }

    public static User getEmployeeWithPositionsAndRelatedAuthorities_Empty() {
        return User.builder()
            .positions(List.of(Position.builder()
                .id(1L)
                .build()))
            .authorities(List.of(Authority.builder()
                .name("Auth")
                .build()))
            .build();
    }

    public static UserLocation getUserLocation() {
        return UserLocation.builder()
            .id(1L)
            .cityEn("Lviv")
            .cityUa("Львів")
            .countryEn("Ukraine")
            .countryUa("Україна")
            .regionUa("Львівська")
            .regionEn("Lvivska")
            .latitude(49.842957)
            .longitude(24.031111)
            .users(Collections.singletonList(getUser()))
            .build();
    }

    public static UserLocation getUserLocation2() {
        return UserLocation.builder()
            .id(2L)
            .cityEn("Ternopil")
            .cityUa("Тернопіль")
            .countryEn("Ukraine")
            .countryUa("Україна")
            .regionUa("Тернопільська")
            .regionEn("Ternopilska")
            .latitude(49.842957)
            .longitude(24.031111)
            .users(Collections.singletonList(getUser()))
            .build();
    }

    public static List<GeocodingResult> getGeocodingResult() {
        List<GeocodingResult> geocodingResults = new ArrayList<>();

        GeocodingResult geocodingResult1 = new GeocodingResult();

        Geometry geometry = new Geometry();
        geometry.location = new LatLng(50.5555555d, 50.5555555d);

        AddressComponent locality = new AddressComponent();
        locality.longName = "fake street";
        locality.types = new AddressComponentType[] {AddressComponentType.LOCALITY};

        AddressComponent streetNumber = new AddressComponent();
        streetNumber.longName = "13";
        streetNumber.types = new AddressComponentType[] {AddressComponentType.STREET_NUMBER};

        AddressComponent region = new AddressComponent();
        region.longName = "fake region";
        region.types = new AddressComponentType[] {AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1};

        AddressComponent sublocality = new AddressComponent();
        sublocality.longName = "fake district";
        sublocality.types = new AddressComponentType[] {AddressComponentType.SUBLOCALITY};

        AddressComponent route = new AddressComponent();
        route.longName = "fake street name";
        route.types = new AddressComponentType[] {AddressComponentType.ROUTE};

        geocodingResult1.addressComponents = new AddressComponent[] {
            locality,
            streetNumber,
            region,
            sublocality,
            route
        };

        geocodingResult1.formattedAddress = "fake address";
        geocodingResult1.geometry = geometry;

        GeocodingResult geocodingResult2 = new GeocodingResult();

        AddressComponent locality2 = new AddressComponent();
        locality2.longName = "fake street";
        locality2.types = new AddressComponentType[] {AddressComponentType.LOCALITY};

        AddressComponent streetNumber2 = new AddressComponent();
        streetNumber2.longName = "13";
        streetNumber2.types = new AddressComponentType[] {AddressComponentType.STREET_NUMBER};

        AddressComponent region2 = new AddressComponent();
        region2.longName = "fake region";
        region2.types = new AddressComponentType[] {AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1};

        AddressComponent sublocality2 = new AddressComponent();
        sublocality2.longName = "fake district";
        sublocality2.types = new AddressComponentType[] {AddressComponentType.SUBLOCALITY};

        AddressComponent route2 = new AddressComponent();
        route2.longName = "fake street name";
        route2.types = new AddressComponentType[] {AddressComponentType.ROUTE};

        geocodingResult2.addressComponents = new AddressComponent[] {
            locality2,
            streetNumber2,
            region2,
            sublocality2,
            route2
        };

        geocodingResult2.formattedAddress = "fake address 2";
        geocodingResult2.geometry = geometry;

        geocodingResults.add(geocodingResult1);
        geocodingResults.add(geocodingResult2);

        return geocodingResults;
    }

    public static UserInfo getUserInfo() {
        return UserInfo.builder()
            .sub("sub")
            .name("name")
            .givenName("given name")
            .familyName("family name")
            .picture("picture")
            .email("test@mail.com")
            .emailVerified("test@mail.com")
            .locale("locale")
            .error("error")
            .errorDescription("error description")
            .build();
    }

    public static GeocodingResult getGeocodingResultWithInsufficientData() {
        GeocodingResult geocodingResult = new GeocodingResult();
        AddressComponent locality = new AddressComponent();
        locality.longName = "fake data";
        locality.types = new AddressComponentType[] {AddressComponentType.UNKNOWN};
        geocodingResult.addressComponents = new AddressComponent[] {locality};

        return geocodingResult;
    }
}
