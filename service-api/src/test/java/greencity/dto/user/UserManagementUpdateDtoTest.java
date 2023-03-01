package greencity.dto.user;

import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserManagementUpdateDtoTest {

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideFieldsAndValidValues")
    void validNameInUserManagementUpdateDtoTest(String name) {
        var dto = UserManagementUpdateDto.builder()
            .name(name)
            .email("test@gmail.com")
            .userStatus(UserStatus.ACTIVATED)
            .role(Role.ROLE_USER)
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserManagementUpdateDto>> constraintViolations =
            validator.validate(dto);

        assertThat(constraintViolations).isEmpty();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideFieldsAndInvalidValues")
    void invalidNameInUserManagementUpdateDtoTest(String name) {
        var dto = UserManagementUpdateDto.builder()
            .name(name)
            .email("test@gmail.com")
            .userStatus(UserStatus.ACTIVATED)
            .role(Role.ROLE_USER)
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserManagementUpdateDto>> constraintViolations =
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
