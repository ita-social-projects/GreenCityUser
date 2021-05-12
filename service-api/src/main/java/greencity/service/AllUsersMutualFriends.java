package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserAllFriendsDto;

public interface AllUsersMutualFriends {
    /**
     * {@inheritDoc}
     */
    PageableDto<UserAllFriendsDto> findAllUsersWithMutualFriends(Long id, int pages, int size);
}
