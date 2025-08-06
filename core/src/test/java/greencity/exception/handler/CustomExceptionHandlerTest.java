package greencity.exception.handler;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@ExtendWith(MockitoExtension.class)
class CustomExceptionHandlerTest {
    @Mock
    MethodArgumentTypeMismatchException mismatchException;
    @Mock
    WebRequest webRequest;
    @Mock
    ErrorAttributes errorAttributes;
    Map<String, Object> objectMap;
    @InjectMocks
    CustomExceptionHandler customExceptionHandler;
    @Mock
    HttpMessageNotReadableException ex;
    @Mock
    HttpHeaders headers;
    @Mock
    FieldError fieldError;
    @Mock
    MethodArgumentNotValidException notValidException;

    @BeforeEach
    void init() {
        objectMap = new HashMap<>();
        objectMap.put("path", "/ownSecurity/restorePassword");
        objectMap.put("message", "test");
        objectMap.put("timestamp", "2021-02-06T17:27:50.569+0000");
        objectMap.put("trace", "Internal Server Error");
    }

    @Test
    void handleWrongPasswordException() {
        WrongPasswordException actual = new WrongPasswordException("password");
        ValidationExceptionDto validationDto = new ValidationExceptionDto(actual.getMessage(), "password");
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        ResponseEntity<Object> body = status.body(validationDto);
        assertEquals(customExceptionHandler.handleWrongPasswordException(actual), body);
    }

    @Test
    void handleWrongEmailException() {
        WrongEmailException actual = new WrongEmailException("email");
        ValidationExceptionDto validationDto = new ValidationExceptionDto(actual.getMessage(), "email");
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        ResponseEntity<Object> body = status.body(validationDto);
        assertEquals(customExceptionHandler.handleWrongEmailException(actual), body);
    }

    @Test
    void handleBadEmailException() {
        UserAlreadyRegisteredException actual = new UserAlreadyRegisteredException("email");
        ValidationExceptionDto validationDto = new ValidationExceptionDto(actual.getMessage(), "email");
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        ResponseEntity<Object> body = status.body(Collections.singletonList(validationDto));
        assertEquals(customExceptionHandler.handleBadEmailException(actual), body);
    }

    @Test
    void handleEmailNotVerified() {
        EmailNotVerified emailNotVerified = new EmailNotVerified("email");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleEmailNotVerified(emailNotVerified, webRequest),
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse));
    }

    @Test
    void handleBadSocialNetworkLinkException() {
        InvalidURLException invalidURLException = new InvalidURLException("test");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleBadSocialNetworkLinkException(invalidURLException, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void testHandleBadSocialNetworkLinkException() {
        BadSocialNetworkLinksException badSocialNetworkLinksException = new BadSocialNetworkLinksException("test");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(
            customExceptionHandler.handleBadSocialNetworkLinkException(badSocialNetworkLinksException, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void testHandleBadRefreshTokenException() {
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleBadRefreshTokenException(webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void testHandleIllegalTokenException() {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("test");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleIllegalArgumentException(illegalArgumentException, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void handleBadRequestException() {
        BadRequestException badRequestException = new BadRequestException("test");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleBadRequestException(badRequestException, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void handleNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("test");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleNotFoundException(notFoundException, webRequest),
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse));
    }

    @Test
    void handleHttpMessageNotReadable() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleHttpMessageNotReadable(
            ex, headers, httpStatus, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void handleConversionFailedException() {
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        exceptionResponse.setMessage("Wrong null. Should be 'null'");
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleConversionFailedException(mismatchException, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void handleMethodArgumentNotValid() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        fieldError = new FieldError("G", "field", "default");
        ValidationExceptionDto validationExceptionDto = new ValidationExceptionDto(fieldError);

        final BindingResult bindingResult = mock(BindingResult.class);

        when(notValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        assertEquals(
            customExceptionHandler.handleMethodArgumentNotValid(notValidException, headers, httpStatus, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonList(validationExceptionDto)));
    }

    @Test
    void handleUserAlreadyHasPasswordException() {
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse),
            customExceptionHandler.handleUserAlreadyHasPasswordException(webRequest));
    }

    @Test
    void handleUserStatusException() {
        BadUserStatusException actual = new BadUserStatusException("user_status");
        ValidationExceptionDto validationDto = new ValidationExceptionDto(actual.getMessage(), "user_status");
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        ResponseEntity<Object> body = status.body(validationDto);
        assertEquals(customExceptionHandler.handleBadUserStatusException(actual), body);
    }

    @Test
    void handleProfilePictureSizeExceededException() {
        MultipartException multipartException = new MultipartException(
            "Maximum upload size exceeded; nested exception is java.lang.IllegalStateException: org.apache.tomcat.util.http.fileupload.FileUploadBase$SizeLimitExceededException: the request was rejected because its size (15478446) exceeds the configured maximum (10485760)");
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        ResponseEntity<Object> body = status.body(multipartException.getMessage());
        assertEquals(customExceptionHandler.handleBadRequestWhenProfilePictureExceeded(multipartException), body);
    }

    @Test
    void handleLanguageNotSupportedException() {
        LanguageNotSupportedException languageNotSupportedException = new LanguageNotSupportedException();
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(
            customExceptionHandler.handleLanguageNotSupportedException(languageNotSupportedException, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
        verify(errorAttributes).getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class));
    }

    @Test
    void handleLanguageNotFoundException() {
        LanguageNotFoundException languageNotFoundException = new LanguageNotFoundException();
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleLanguageNotFoundException(languageNotFoundException, webRequest),
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse));
        verify(errorAttributes).getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class));
    }

    @Test
    void handleUserDeactivationExceptionTest() {
        UserDeactivationException actual = new UserDeactivationException("Some string");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleUserDeactivationException(actual, webRequest),
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse));
    }

    @Test
    void handleBase64DecodedExceptionTest() {
        Base64DecodedException actual = new Base64DecodedException("Some string");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleBase64DecodedException(actual, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void handleUserBlockedExceptionTest() {
        UserBlockedException actual = new UserBlockedException("Some string");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleUserBlockedException(actual, webRequest),
            ResponseEntity.status(HttpStatus.LOCKED).body(exceptionResponse));
    }

    @Test
    void handleWrongCaptchaExceptionTest() {
        WrongCaptchaException actual = new WrongCaptchaException("Some string");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleWrongCaptchaException(actual, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
    }

    @Test
    void handleGreenCityServiceException500Test() {
        GreenCityServiceException actual = new GreenCityServiceException("Some error message");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest), any(ErrorAttributeOptions.class))).thenReturn(
            objectMap);
        ResponseEntity<Object> response = customExceptionHandler.handleGreenCityServiceException(actual, webRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse), response);
    }

    @Test
    void handleGreenCityServiceException503Test() {
        WebClientRequestException actualException = new WebClientRequestException(
            new IOException("Connection failed"),
            HttpMethod.GET,
            URI.create("http://localhost"),
            HttpHeaders.EMPTY);
        ResponseEntity<Object> response =
            customExceptionHandler.handleGreenCityServiceException(actualException, webRequest);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals(ErrorMessage.GREENCITY_APP_UNAVAILABLE, body.get(AppConstant.MESSAGE));
    }

    @Test
    void handlePasswordsDoNotMatchesExceptionTest() {
        String errorMessage = "Passwords do not match";
        PasswordsDoNotMatchesException exception = new PasswordsDoNotMatchesException(errorMessage);
        ResponseEntity<Object> response = customExceptionHandler.handlePasswordsDoNotMatchesException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ValidationExceptionDto.class, response.getBody());
        ValidationExceptionDto validationExceptionDto = (ValidationExceptionDto) response.getBody();
        assertEquals(AppConstant.PASSWORD, validationExceptionDto.getName());
        assertEquals(errorMessage, validationExceptionDto.getMessage());
    }

    @Test
    void handleConstraintViolationExceptionTest() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        when(violation1.getMessage()).thenReturn("must not be null");
        when(violation2.getMessage()).thenReturn("must be at least 5 characters");
        Set<ConstraintViolation<?>> constraintViolations = Set.of(violation1, violation2);
        ConstraintViolationException exception = new ConstraintViolationException(constraintViolations);
        when(errorAttributes.getErrorAttributes(eq(webRequest), any(ErrorAttributeOptions.class)))
            .thenReturn(objectMap);
        ResponseEntity<Object> response =
            customExceptionHandler.handleConstraintViolationException(exception, webRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ExceptionResponse.class, response.getBody());
        ExceptionResponse exceptionResponse = (ExceptionResponse) response.getBody();
        assertTrue(exceptionResponse.getMessage().contains("must not be null"));
        assertTrue(exceptionResponse.getMessage().contains("must be at least 5 characters"));
        assertEquals("/ownSecurity/restorePassword", exceptionResponse.getPath());
        assertEquals("Internal Server Error", exceptionResponse.getTrace());
    }

    @Test
    void handleBadUpdateRequestExceptionTest() {
        BadUpdateRequestException exception = new BadUpdateRequestException("Some string");
        when(errorAttributes.getErrorAttributes(eq(webRequest), any(ErrorAttributeOptions.class)))
            .thenReturn(objectMap);
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse),
            customExceptionHandler.handleBadUpdateRequestException(exception, webRequest));
    }

    @Test
    void handleResourceNotFoundExceptionTest() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Some string");
        when(errorAttributes.getErrorAttributes(eq(webRequest), any(ErrorAttributeOptions.class)))
            .thenReturn(objectMap);
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        exceptionResponse.setMessage(exception.getMessage());
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse),
            customExceptionHandler.handleResourceNotFoundException(exception, webRequest));
    }

}