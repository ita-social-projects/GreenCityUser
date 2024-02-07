package greencity.filters;

import greencity.dto.user.UserManagementViewDto;
import greencity.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserSpecificationTest {
    @Mock
    private Root<User> root;
    @Mock
    private CriteriaQuery<User> criteriaQuery;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate expected;
    @Mock
    private Path<Object> objectPathExpected;
    @Mock
    private Expression<Integer> as;
    List<SearchCriteria> searchCriteriaList;
    UserSpecification userSpecification;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        searchCriteriaList = new ArrayList<>();
        UserManagementViewDto userViewDto = UserManagementViewDto.builder()
            .id("1")
            .name("test")
            .email("test@ukr.net")
            .userCredo("test")
            .role("1")
            .userStatus("2")
            .build();
        searchCriteriaList.add(SearchCriteria.builder()
            .key("id")
            .type("id")
            .value(userViewDto.getId())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("name")
            .type("name")
            .value(userViewDto.getName())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("email")
            .type("email")
            .value(userViewDto.getEmail())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("userCredo")
            .type("userCredo")
            .value(userViewDto.getUserCredo())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("role")
            .type("role")
            .value(userViewDto.getRole())
            .build());
        searchCriteriaList.add(SearchCriteria.builder()
            .key("userStatus")
            .type("userStatus")
            .value(userViewDto.getUserStatus())
            .build());
        userSpecification = new UserSpecification(searchCriteriaList);
    }

    @Test
    void toPredicate() {
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteriaList.getFirst().getKey())).thenReturn(objectPathExpected);
        when(criteriaBuilder.equal(objectPathExpected, searchCriteriaList.getFirst().getValue()))
            .thenThrow(NumberFormatException.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.disjunction()).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteriaList.get(1).getKey())).thenReturn(objectPathExpected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteriaList.get(2).getKey())).thenReturn(objectPathExpected);
        when(criteriaBuilder.like(any(), eq("%" + searchCriteriaList.get(2).getValue() + "%"))).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteriaList.get(3).getKey())).thenReturn(objectPathExpected);
        when(criteriaBuilder.like(any(), eq("%" + searchCriteriaList.get(3).getValue() + "%"))).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteriaList.get(4).getKey())).thenReturn(objectPathExpected);
        when(objectPathExpected.as(Integer.class)).thenReturn(as);
        when(criteriaBuilder.equal(as, searchCriteriaList.get(4).getValue())).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(root.get(searchCriteriaList.get(5).getKey())).thenReturn(objectPathExpected);
        when(objectPathExpected.as(Integer.class)).thenReturn(as);
        when(criteriaBuilder.equal(as, searchCriteriaList.get(5).getValue())).thenReturn(expected);
        when(criteriaBuilder.and(expected, expected)).thenReturn(expected);
        Predicate actual = userSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(expected, actual);
    }
}