package greencity.mapping;

import greencity.dto.language.LanguageVO;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.user.UserVO;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserVOAdvancedDtoMapper extends AbstractConverter<User, UserVOAdvancedDto> {
    @Override
    protected UserVOAdvancedDto convert(User user) {
        Long userId = user.getId();

        List<SocialNetworkVO> socialNetworks = user.getSocialNetworks() != null ? user.getSocialNetworks()
            .stream().map(socialNetwork -> SocialNetworkVO.builder()
                .id(socialNetwork.getId())
                .url(socialNetwork.getUrl())
                .user(UserVO.builder()
                    .id(socialNetwork.getUser().getId())
                    .email(socialNetwork.getUser().getEmail())
                    .build())
                .socialNetworkImage(SocialNetworkImageVO.builder()
                    .id(socialNetwork.getSocialNetworkImage().getId())
                    .imagePath(socialNetwork.getSocialNetworkImage().getImagePath())
                    .hostPath(socialNetwork.getSocialNetworkImage().getHostPath())
                    .build())
                .build())
            .toList() : new ArrayList<>();

        UserVOAdvancedDto userVOAdvancedDto = new UserVOAdvancedDto();
        userVOAdvancedDto.setId(userId);
        userVOAdvancedDto.setName(user.getName());
        userVOAdvancedDto.setEmail(user.getEmail());
        userVOAdvancedDto.setRole(user.getRole());
        userVOAdvancedDto.setUserCredo(user.getUserCredo());
        userVOAdvancedDto.setUserStatus(user.getUserStatus());
        userVOAdvancedDto.setDateOfRegistration(user.getDateOfRegistration());
        userVOAdvancedDto.setProfilePicturePath(user.getProfilePicturePath());
        userVOAdvancedDto.setLanguage(LanguageVO.builder()
            .id(user.getLanguage().getId())
            .code(user.getLanguage().getCode())
            .build());
        userVOAdvancedDto.setFirstName(user.getFirstName());
        userVOAdvancedDto.setSocialNetworks(socialNetworks);

        return userVOAdvancedDto;
    }
}
