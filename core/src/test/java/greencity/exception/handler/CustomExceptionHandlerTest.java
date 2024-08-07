package greencity.exception.handler;

import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadSocialNetworkLinksException;
import greencity.exception.exceptions.BadUserStatusException;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.exception.exceptions.InsufficientLocationDataException;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.LanguageNotSupportedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyRegisteredException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.exception.exceptions.WrongPasswordException;
import greencity.exception.exceptions.GoogleApiException;
import greencity.exception.exceptions.UserDeactivationException;
import greencity.exception.exceptions.Base64DecodedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

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
        ValidationExceptionDto validationDtoField = new ValidationExceptionDto(fieldError);
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
    void handleWrongIdException() {
        WrongIdException wrongIdException = new WrongIdException("test");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleWrongIdException(wrongIdException, webRequest),
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

    /*
     * @Test void handleMethodArgumentNotValid() { HttpStatus httpStatus =
     * HttpStatus.BAD_REQUEST; FieldError fieldError = new FieldError("G", "field",
     * "default"); ValidationExceptionDto validationExceptionDto = new
     * ValidationExceptionDto(fieldError);
     * when(notValidException.getBindingResult()).thenReturn(bindingResult);
     * when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(
     * fieldError)); assertEquals(
     * customExceptionHandler.handleMethodArgumentNotValid(notValidException,
     * headers, httpStatus, webRequest),
     * ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonList(
     * validationExceptionDto))); }
     */

    @Test
    void handleMethodArgumentNotValid() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        FieldError fieldError = new FieldError("G", "field", "default");
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
    void handleLanguageNotFoundException() {
        LanguageNotSupportedException languageNotSupportedException = new LanguageNotSupportedException();
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleLanguageNotFoundException(languageNotSupportedException, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
        verify(errorAttributes).getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class));
    }

    @Test
    void handleGoogleApiException() {
        GoogleApiException actual = new GoogleApiException("Geocoding result was not found");
        ValidationExceptionDto validationDto = new ValidationExceptionDto("Google API", actual.getMessage());
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.NOT_FOUND);
        ResponseEntity<Object> body = status.body(validationDto);
        assertEquals(customExceptionHandler.handleGoogleApiException(actual), body);
    }

    @Test
    void handleGoogleApiException_GeocodingResultBadRequest_ReturnsBadRequest() {
        GoogleApiException actual = new GoogleApiException("Some string");
        ValidationExceptionDto validationDto = new ValidationExceptionDto("Google API", actual.getMessage());
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        ResponseEntity<Object> body = status.body(validationDto);
        assertEquals(customExceptionHandler.handleGoogleApiException(actual), body);
    }

    @Test
    void handleInsufficientLocationDataExceptionTest() {
        InsufficientLocationDataException actual = new InsufficientLocationDataException("Some string");
        ExceptionResponse exceptionResponse = new ExceptionResponse(objectMap);
        when(errorAttributes.getErrorAttributes(eq(webRequest),
            any(ErrorAttributeOptions.class))).thenReturn(objectMap);
        assertEquals(customExceptionHandler.handleInsufficientLocationDataException(actual, webRequest),
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse));
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
}