package greencity.filters;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface CustomSpecification<T> extends Specification<T> {
    /**
     * Used for build predicate for id filter.
     */
    default Predicate getIdPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        try {
            return criteriaBuilder
                .equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
        } catch (NumberFormatException ex) {
            return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction()
                : criteriaBuilder.disjunction();
        }
    }

    /**
     * Used for build predicate for string fields filter.
     */
    default Predicate getStringPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction()
            : criteriaBuilder.like(root.get(searchCriteria.getKey()),
                "%" + searchCriteria.getValue() + "%");
    }

    /**
     * Used for build predicate for role and status filter.
     */
    default Predicate getEnumPredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
        SearchCriteria searchCriteria) {
        return searchCriteria.getValue().toString().trim().equals("") ? criteriaBuilder.conjunction()
            : criteriaBuilder.equal(root.get(searchCriteria.getKey()).as(Integer.class),
                searchCriteria.getValue());
    }
}
