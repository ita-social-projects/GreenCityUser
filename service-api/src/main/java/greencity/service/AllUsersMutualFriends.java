package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserAllFriendsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.w3c.dom.stylesheets.LinkStyle;
import java.util.List;

public interface AllUsersMutualFriends {
    /**
     * {@inheritDoc}
     */
    PageableDto<UserAllFriendsDto> findAllUsersWithMutualFriends(Long id, int pages, int size);
}
