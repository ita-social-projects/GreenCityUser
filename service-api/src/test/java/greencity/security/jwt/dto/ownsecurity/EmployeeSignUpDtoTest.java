package greencity.security.jwt.dto.ownsecurity;

import greencity.security.dto.ownsecurity.EmployeeSignUpDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmployeeSignUpDtoTest {

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideFieldsAndValidValues")
    void validNameInEmployeeSignUpDtoTest(String name) {
        var dto = EmployeeSignUpDto.builder()
            .name(name)
            .email("test@gmail.com")
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<EmployeeSignUpDto>> constraintViolations =
            validator.validate(dto);

        assertThat(constraintViolations).isEmpty();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideFieldsAndInvalidValues")
    void invalidNameInEmployeeSignUpDtoTest(String name) {
        var dto = EmployeeSignUpDto.builder()
            .name(name)
            .email("test@gmail.com")
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<EmployeeSignUpDto>> constraintViolations =
            validator.validate(dto);

        assertThat(constraintViolations).hasSize(1);
    }

    private static Stream<Arguments> provideFieldsAndValidValues() {
        return Stream.of(
            Arguments.of("T"),
            Arguments.of("Tt"),
            Arguments.of("T.t"),
            Arguments.of("T-"),
            Arguments.of("T'"),
            Arguments.of("T'’"),
            Arguments.of("T'’.t"),
            Arguments.of("T2"),
            Arguments.of("ІіЇїҐґЄє"),
            Arguments.of("ІіЇїҐґ Єє"),
            Arguments.of("Тест"),
            Arguments.of("Test"));
    }

    private static Stream<Arguments> provideFieldsAndInvalidValues() {
        return Stream.of(
            Arguments.of(""),
            Arguments.of(" "),
            Arguments.of("t"),
            Arguments.of("1"),
            Arguments.of("T."),
            Arguments.of("T.."),
            Arguments.of("T--"),
            Arguments.of("ЭэЁёЪъЫы"),
            Arguments.of("@#$"),
            Arguments.of("1test"),
            Arguments.of("test"),
            Arguments.of("Testttttttttttttttttttttttttttt"));
    }
}
