package greencity;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.language.LanguageVO;
import greencity.dto.socialnetwork.SocialNetworkImageRequestDTO;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.dto.user.UserVOShort;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.enums.Role;
import greencity.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ModelUtils {
    public static final UserVO TEST_USER_VO = createUserVO();

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .build();
    }

    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper();

    }

    public static UserManagementDto getUserManagementDto() {
        return UserManagementDto.builder()
            .id(1L)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .email(TestConst.EMAIL)
            .userCredo(TestConst.CREDO)
            .build();
    }

    public static UserManagementUpdateDto getUserManagementUpdateDto() {
        return UserManagementUpdateDto.builder()
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .email(TestConst.EMAIL)
            .userCredo(TestConst.CREDO)
            .build();
    }

    public static PageableAdvancedDto<UserManagementDto> getPageableAdvancedDto() {
        return new PageableAdvancedDto<>(List.of(getUserManagementDto()), 1L, 1, 1, 1, false, false, true, true);
    }

    private static UserVO createUserVO() {
        return UserVO.builder().email("test@gmail.com").role(Role.ROLE_ADMIN).build();
    }

    public static UserVOAdvancedDto getUserVOAdvancedDto() {
        return UserVOAdvancedDto.builder()
            .id(13L)
            .name("user")
            .email("taras@gmail.com")
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .dateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47))
            .firstName("Taras")
            .language(LanguageVO.builder()
                .id(2L)
                .code("en")
                .build())
            .socialNetworks(getSocialNetworkVOs())
            .build();
    }

    public static List<SocialNetworkVO> getSocialNetworkVOs() {
        SocialNetworkVO socialNetworkVO1 = SocialNetworkVO.builder()
            .id(9L)
            .url("http://test.com.ua")
            .user(getUserVoShort())
            .socialNetworkImage(getOneSocialNetworkImageVO())
            .build();

        SocialNetworkVO socialNetworkVO2 = SocialNetworkVO.builder()
            .id(10L)
            .url("http://test-test.com.ua")
            .user(getUserVoShort())
            .socialNetworkImage(getOneSocialNetworkImageVO())
            .build();

        return List.of(socialNetworkVO1, socialNetworkVO2);
    }

    public static SocialNetworkImageVO getOneSocialNetworkImageVO() {
        return SocialNetworkImageVO.builder()
            .id(13L)
            .imagePath("http://test-test.com.ua")
            .hostPath("hostPath2")
            .build();
    }

    public static UserVO getUserVoShort() {
        return UserVO.builder()
            .id(1L)
            .email("taras@gmail.com")
            .build();
    }

    public static SocialNetworkImageRequestDTO getSocialNetworkImageRequestDTO() {
        return SocialNetworkImageRequestDTO.builder()
            .imagePath("http://someimage.ua")
            .hostPath("somehost")
            .build();
    }

    public static SocialNetworkImageResponseDTO getSocialNetworkImageResponseDTO() {
        return SocialNetworkImageResponseDTO.builder()
            .imagePath("http://someimage.ua")
            .hostPath("somehost")
            .id(5L)
            .build();
    }

    public static UserVOShort getUserVOShortDto() {
        return UserVOShort.builder()
            .id(13L)
            .name("user")
            .email("taras@gmail.com")
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .languageVO(LanguageVO.builder()
                .id(2L)
                .code("en")
                .build())
            .build();
    }
}
