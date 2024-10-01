package greencity.filters;

import greencity.dto.user.UserManagementViewDto;
import greencity.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomSpecificationTest {
    @Mock
    private Root<User> root;
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
    void getIdPredicate() {
        when(root.get(searchCriteriaList.getFirst().getKey())).thenReturn(objectPathExpected);
        when(criteriaBuilder.equal(objectPathExpected, searchCriteriaList.getFirst().getValue()))
            .thenThrow(NumberFormatException.class);
        when(criteriaBuilder.disjunction()).thenReturn(expected);
        Predicate actual = userSpecification.getIdPredicate(root, criteriaBuilder, searchCriteriaList.getFirst());
        assertEquals(expected, actual);
    }

    @Test
    void getStringPredicate() {
        when(root.get(searchCriteriaList.get(1).getKey())).thenReturn(objectPathExpected);
        when(criteriaBuilder.like(any(), eq("%" + searchCriteriaList.get(1).getValue() + "%"))).thenReturn(expected);
        Predicate actual = userSpecification.getStringPredicate(root, criteriaBuilder, searchCriteriaList.get(1));

        assertEquals(expected, actual);
    }

    @Test
    void getEnumPredicate() {
        when(root.get(searchCriteriaList.get(5).getKey())).thenReturn(objectPathExpected);
        when(objectPathExpected.as(Integer.class)).thenReturn(as);
        when(criteriaBuilder.equal(as, searchCriteriaList.get(5).getValue())).thenReturn(expected);
        Predicate actual = userSpecification.getEnumPredicate(root, criteriaBuilder, searchCriteriaList.get(5));
        assertEquals(expected, actual);
    }
}