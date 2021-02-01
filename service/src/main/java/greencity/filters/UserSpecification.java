package greencity.filters;

import greencity.entity.User;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@AllArgsConstructor
public class UserSpecification implements CustomSpecification<User> {
    private transient List<SearchCriteria> searchCriteriaList;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate allPredicate = criteriaBuilder.conjunction();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getType().equals("id")) {
                allPredicate = criteriaBuilder.and(allPredicate, getIdPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("name")) {
                allPredicate =
                    criteriaBuilder.and(allPredicate, getStringPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("email")) {
                allPredicate =
                    criteriaBuilder.and(allPredicate, getStringPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("userCredo")) {
                allPredicate =
                    criteriaBuilder.and(allPredicate, getStringPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("role")) {
                allPredicate =
                    criteriaBuilder.and(allPredicate, getEnumPredicate(root, criteriaBuilder, searchCriteria));
            }
            if (searchCriteria.getType().equals("userStatus")) {
                allPredicate =
                    criteriaBuilder.and(allPredicate, getEnumPredicate(root, criteriaBuilder, searchCriteria));
            }
        }
        return allPredicate;
    }
}
