package greencity.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchCriteriaTest {
    SearchCriteria searchCriteria;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        searchCriteria = new SearchCriteria("test", "test", "test");
    }

    @Test
    void testEqualsTest() {
        SearchCriteria searchCriteriaExpected = new SearchCriteria("test", "test", "test");
        Object actual = searchCriteria.equals(searchCriteriaExpected);
        Object expected = searchCriteria.equals(searchCriteriaExpected);
        assertEquals(expected, actual);
    }

    @Test
    void testHashCodeTest() {
        SearchCriteria searchCriteriaExpected = new SearchCriteria("test", "test", "test");
        int actual = searchCriteria.hashCode();
        int expected = searchCriteriaExpected.hashCode();
        assertEquals(expected, actual);
    }

    @Test
    void toStringTest() {
        SearchCriteria searchCriteriaBuild = SearchCriteria.builder()
            .key("test")
            .type("test")
            .value("test")
            .build();
        assertEquals(searchCriteriaBuild.toString(), searchCriteria.toString());
        searchCriteria.setValue("test1");
    }
}