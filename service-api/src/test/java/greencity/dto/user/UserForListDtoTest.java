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

class UserForListDtoTest {

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideFieldsAndValidValues")
    void validNameInUserForListDtoTest(String name) {
        var dto = UserForListDto.builder()
            .id(1L)
            .name(name)
            .email("test@gmail.com")
            .userStatus(UserStatus.ACTIVATED)
            .role(Role.ROLE_USER)
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserForListDto>> constraintViolations =
            validator.validate(dto);

        assertThat(constraintViolations).isEmpty();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideFieldsAndInvalidValues")
    void invalidNameInUserForListDtoTest(String name) {
        var dto = UserForListDto.builder()
            .id(1L)
            .name(name)
            .email("test@gmail.com")
            .userStatus(UserStatus.ACTIVATED)
            .role(Role.ROLE_USER)
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserForListDto>> constraintViolations =
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
            Arguments.of("Імʼя"),
            Arguments.of("Тест"),
            Arguments.of("Тест123"),
            Arguments.of("Ґ.Ї.Є"),
            Arguments.of("Тест-Тест"),
            Arguments.of("ІмʼяʼТест"),
            Arguments.of("АртемʼЄвгенович"),
            Arguments.of("Test"),
            Arguments.of("Test.Name"),
            Arguments.of("Test-Name"),
            Arguments.of("Name'Last"),
            Arguments.of("Name2"),
            Arguments.of("Імʼя.Тест"),
            Arguments.of("ҐрімʼТест"),
            Arguments.of("ЄвгенҐ123"),
            Arguments.of("Ім'я-Тест"),
            Arguments.of("Test-Test"),
            Arguments.of("User123"),
            Arguments.of("Імʼя2Тест"),
            Arguments.of("ТестʼІмʼя"),
            Arguments.of("Євген.Тест"));
    }

    private static Stream<Arguments> provideFieldsAndInvalidValues() {
        return Stream.of(
            Arguments.of("."),
            Arguments.of(".."),
            Arguments.of("-"),
            Arguments.of("'"),
            Arguments.of("ʼ"),
            Arguments.of("ʼʼ"),
            Arguments.of("--"),
            Arguments.of("..Тест"),
            Arguments.of("Тест.."),
            Arguments.of("Тест..Імʼя"),
            Arguments.of("Тест--Імʼя"),
            Arguments.of("Тест.."),
            Arguments.of("Тест..Тест"),
            Arguments.of("Тест--Тест"),
            Arguments.of("Тест@Імʼя"),
            Arguments.of("Імʼя#Тест"),
            Arguments.of("Імʼя_Тест!"),
            Arguments.of("ІмʼяТестЗанадтоДовгеБільше30Символів"),
            Arguments.of("12345"),
            Arguments.of(" Імʼя"),
            Arguments.of(".."),
            Arguments.of("1234567890123456789012345678901"),
            Arguments.of(""),
            Arguments.of(" "),
            Arguments.of("T.."),
            Arguments.of("T--"),
            Arguments.of("T."),
            Arguments.of("1test"),
            Arguments.of("@#$"),
            Arguments.of("Testttttttttttttttttttttttttttt"));
    }
}
