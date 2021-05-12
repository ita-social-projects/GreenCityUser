package greencity.service;

import greencity.client.RestClient;
import greencity.dto.PageableDto;
import greencity.dto.user.UserAllFriendsDto;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AllUsersWithMutualFriendsServiceImpl implements AllUsersMutualFriends {
    private final JdbcTemplate jdbcTemplate;
    private final RestClient restClient;

    @Override
    public PageableDto<UserAllFriendsDto> findAllUsersWithMutualFriends(Long id, int pages, int size) {
        int numberOfElements = 0;
        int offset = pages * size;
        List<Map<String, Object>> maps = null;
        try {
            maps = jdbcTemplate.queryForList("SELECT * FROM (SELECT U2.USER_ID, COUNT(*) AS MUTUAL_COUNT"
                + " FROM users_friends U1\n"
                + "LEFT JOIN users_friends U2 on U1.friend_id = U2.friend_id\n"
                + "left join users on users.id = u2.user_id\n"
                + "WHERE U1.user_id =" + id + " GROUP BY U2.user_id Having u2.user_id not in (" + id + ")\n"
                + "ORDER BY MUTUAL_COUNT DESC) u2 JOIN users u1 on u2.user_id = u1.id\n"
                + "LIMIT " + size + " OFFSET " + offset);
            numberOfElements = jdbcTemplate.queryForObject("SELECT count(*) "
                + " FROM (SELECT U2.USER_ID, COUNT(*) AS MUTUAL_COUNT"
                + " FROM users_friends U1\n"
                + "LEFT JOIN users_friends U2 on U1.friend_id = U2.friend_id\n"
                + "left join users on users.id = u2.user_id\n"
                + "WHERE U1.user_id =" + id + " GROUP BY U2.user_id Having u2.user_id not in (" + id + ")\n"
                + "ORDER BY MUTUAL_COUNT DESC) u2 JOIN users u1 on u2.user_id = u1.id\n", Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<UserAllFriendsDto> result = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            result.add(UserAllFriendsDto.builder()
                .id((Long) map.get("id"))
                .name((String) map.get("name"))
                .city((String) map.get("city"))
                .rating((Double) map.get("rating"))
                .mutualFriends((Long) map.get("mutual_count"))
                .profilePicture((String) map.get("profile_picture"))
                .build());
        }
        int totalPages = (numberOfElements / size) + 1;
        return new PageableDto<>(
            result,
            size,
            pages,
            totalPages);
    }
}
