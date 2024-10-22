package greencity.exception.handler;

import greencity.constant.AppConstant;
import greencity.exception.exceptions.BadRefreshTokenException;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadSocialNetworkLinksException;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.BadUserStatusException;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.exception.exceptions.InsufficientLocationDataException;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.LanguageNotSupportedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.PasswordsDoNotMatchesException;
import greencity.exception.exceptions.UserAlreadyHasPasswordException;
import greencity.exception.exceptions.UserAlreadyRegisteredException;
import greencity.exception.exceptions.UserBlockedException;
import greencity.exception.exceptions.WrongCaptchaException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongPasswordException;
import greencity.exception.exceptions.GoogleApiException;
import greencity.exception.exceptions.UserDeactivationException;
import greencity.exception.exceptions.Base64DecodedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom exception handler.
 */
@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private final ErrorAttributes errorAttributes;

    /**
     * Method intercept exception {@link ConstraintViolationException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolationException(
        ConstraintViolationException ex,
        WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        String detailedMessage = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(" "));
        exceptionResponse.setMessage(detailedMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link IllegalArgumentException}.
     *
     * @param ex      the IllegalArgumentException that was thrown
     * @param request the WebRequest that triggered the exception
     * @return a ResponseEntity containing the {@link ExceptionResponse} with
     *         details about the error and a 400 Bad Request HTTP status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<Object> handleIllegalArgumentException(
        IllegalArgumentException ex,
        WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        exceptionResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link BadRequestException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex,
        WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Exception handler for BadUpdateRequestException.
     *
     * @param exception which is being intercepted
     * @param request   contains details about occurred exception
     * @return ResponseEntity which contains details about exception and 400 status
     *         code
     */
    @ExceptionHandler(BadUpdateRequestException.class)
    public final ResponseEntity<Object> handleBadUpdateRequestException(
        BadUpdateRequestException exception, WebRequest request) {
        log.trace(exception.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link NotFoundException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link MethodArgumentTypeMismatchException}.
     *
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<Object> handleConversionFailedException(
        @NonNull MethodArgumentTypeMismatchException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        String propName = ex.getName();
        String className = null;
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null) {
            className = requiredType.getSimpleName();
        }
        String message = "Wrong %s. Should be '%s'".formatted(propName, className);
        exceptionResponse.setMessage(message);
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link BadRefreshTokenException}.
     *
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(BadRefreshTokenException.class)
    public final ResponseEntity<Object> handleBadRefreshTokenException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link UserAlreadyRegisteredException}.
     *
     * @param ex Exception witch should be intercepted.
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public final ResponseEntity<Object> handleBadEmailException(UserAlreadyRegisteredException ex) {
        ValidationExceptionDto validationExceptionDto =
            new ValidationExceptionDto(AppConstant.REGISTRATION_EMAIL_FIELD_NAME, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Collections.singletonList(validationExceptionDto));
    }

    /**
     * Method intercept exception {@link BadSocialNetworkLinksException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(BadSocialNetworkLinksException.class)
    public final ResponseEntity<Object> handleBadSocialNetworkLinkException(
        BadSocialNetworkLinksException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method intercept exception {@link InvalidURLException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(InvalidURLException.class)
    public final ResponseEntity<Object> handleBadSocialNetworkLinkException(InvalidURLException ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method interceptor exception {@link EmailNotVerified}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(EmailNotVerified.class)
    public final ResponseEntity<Object> handleEmailNotVerified(EmailNotVerified ex,
        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    /**
     * Method interceptor exception {@link WrongPasswordException}.
     *
     * @param ex Exception witch should be intercepted
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(WrongPasswordException.class)
    public final ResponseEntity<Object> handleWrongPasswordException(WrongPasswordException ex) {
        ValidationExceptionDto validationExceptionDto =
            new ValidationExceptionDto(AppConstant.PASSWORD, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationExceptionDto);
    }

    /**
     * Method interceptor exception {@link WrongEmailException}.
     *
     * @param ex Exception witch should be intercepted.
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(WrongEmailException.class)
    public final ResponseEntity<Object> handleWrongEmailException(WrongEmailException ex) {
        ValidationExceptionDto validationExceptionDto =
            new ValidationExceptionDto(AppConstant.REGISTRATION_EMAIL_FIELD_NAME, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationExceptionDto);
    }

    /*
     * Customize the response for HttpMessageNotReadableException.
     *
     * @param ex the exception
     *
     * @param headers the headers to be written to the response
     *
     * @param status the selected response status
     *
     * @param request the current request
     *
     * @return a {@code ResponseEntity} message
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {
        List<ValidationExceptionDto> collect =
            ex.getBindingResult().getFieldErrors().stream()
                .map(ValidationExceptionDto::new)
                .toList();
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(collect);
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest) {
        return new HashMap<>(errorAttributes.getErrorAttributes(webRequest,
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)));
    }

    /**
     * Method interceptor exception {@link PasswordsDoNotMatchesException}.
     *
     * @param ex Exception witch should be intercepted.
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(PasswordsDoNotMatchesException.class)
    public final ResponseEntity<Object> handlePasswordsDoNotMatchesException(
        PasswordsDoNotMatchesException ex) {
        ValidationExceptionDto validationExceptionDto =
            new ValidationExceptionDto(AppConstant.PASSWORD, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationExceptionDto);
    }

    /**
     * Method interceptor exception {@link UserAlreadyHasPasswordException}.
     *
     * @param request {@link WebRequest} in which the exception has been thrown.
     * @return {@link ResponseEntity} contains http status and body with exception
     *         message.
     */
    @ExceptionHandler(UserAlreadyHasPasswordException.class)
    public final ResponseEntity<Object> handleUserAlreadyHasPasswordException(WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Method interceptor exception {@link BadUserStatusException}.
     *
     * @param ex Exception witch should be intercepted
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(BadUserStatusException.class)
    public final ResponseEntity<Object> handleBadUserStatusException(BadUserStatusException ex) {
        ValidationExceptionDto validationExceptionDto =
            new ValidationExceptionDto(AppConstant.USER_STATUS, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationExceptionDto);
    }

    /**
     * Method interceptor exception {@link MultipartException}.
     *
     * @param me Exception witch should be intercepted
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(MultipartException.class)
    public final ResponseEntity<Object> handleBadRequestWhenProfilePictureExceeded(
        MultipartException me) {
        log.error("Error when profile picture was being uploaded {}", me);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(me.getMessage());
    }

    /**
     * Method intercept exception {@link LanguageNotSupportedException}.
     *
     * @param ex      Exception witch should be intercepted.
     * @param request contain detail about occur exception
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler({LanguageNotSupportedException.class})
    public ResponseEntity<Object> handleLanguageNotFoundException(LanguageNotSupportedException ex,
        WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Method interceptor exception {@link GoogleApiException}.
     *
     * @param googleApiException Exception witch should be intercepted
     * @return ResponseEntity witch contain http status and body with message of
     *         exception.
     */
    @ExceptionHandler(GoogleApiException.class)
    public ResponseEntity<Object> handleGoogleApiException(GoogleApiException googleApiException) {
        ValidationExceptionDto validationExceptionDto =
            new ValidationExceptionDto(AppConstant.GOOGLE_API, googleApiException.getMessage());
        if (googleApiException.getMessage() != null
            && googleApiException.getMessage().contains("Geocoding result was not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(validationExceptionDto);
        } else {
            validationExceptionDto.setMessage(googleApiException.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationExceptionDto);
        }
    }

    /**
     * Exception handler for InsufficientLocationDataException.
     *
     * @param exception which is being intercepted
     * @param request   contains details about occurred exception
     * @return ResponseEntity which contains details about exception and 400 status
     *         code
     */
    @ExceptionHandler(InsufficientLocationDataException.class)
    public final ResponseEntity<Object> handleInsufficientLocationDataException(
        InsufficientLocationDataException exception, WebRequest request) {
        log.error(exception.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Handles UserDeactivationException and returns a ResponseEntity with a
     * Forbidden status code and an ExceptionResponse body containing error details.
     *
     * @param exception the UserDeactivationException instance
     * @param request   the current web request
     * @return a ResponseEntity containing the HTTP status code and error response
     *         body
     */
    @ExceptionHandler(UserDeactivationException.class)
    public ResponseEntity<Object> handleUserDeactivationException(
        UserDeactivationException exception, WebRequest request) {
        log.error(exception.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    /**
     * Handles exceptions of type {@link Base64DecodedException}.
     *
     * @param exception the exception that was thrown due to issues with Base64
     *                  decoding.
     * @param request   the current {@link WebRequest} in which the exception
     *                  occurred.
     * @return a {@link ResponseEntity} containing an {@link ExceptionResponse} with
     *         error attributes and an HTTP status of 400 (Bad Request).
     */
    @ExceptionHandler(Base64DecodedException.class)
    public ResponseEntity<Object> handleBase64DecodedException(Base64DecodedException exception,
        WebRequest request) {
        log.error(exception.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    /**
     * Handles exceptions of type {@link UserBlockedException}.
     *
     * @param exception the UserBlockedException instance
     * @param request   the current web request
     * @return a ResponseEntity containing the HTTP status code and error response
     *         body
     */
    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<Object> handleUserBlockedException(UserBlockedException exception,
        WebRequest request) {
        log.error(exception.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));

        return ResponseEntity.status(HttpStatus.LOCKED).body(exceptionResponse);
    }

    /**
     * Handles exceptions of type {@link WrongCaptchaException}.
     *
     * @param exception the WrongCaptchaException instance
     * @param request   the current web request
     * @return a ResponseEntity containing the HTTP status code and error response
     *         body
     */
    @ExceptionHandler(WrongCaptchaException.class)
    public ResponseEntity<Object> handleWrongCaptchaException(WrongCaptchaException exception,
        WebRequest request) {
        log.error(exception.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
}
