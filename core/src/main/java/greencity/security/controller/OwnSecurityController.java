package greencity.security.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.security.dto.ownsecurity.*;
import greencity.dto.user.UserAdminRegistrationDto;
import greencity.dto.user.UserManagementDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.PasswordRecoveryService;
import greencity.security.service.VerifyEmailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

import static greencity.constant.ErrorMessage.*;
import static greencity.constant.ValidationConstants.USER_CREATED;

/**
 * Controller that provides our sign-up and sign-in logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@RestController
@RequestMapping("/ownSecurity")
@Validated
@Slf4j
public class OwnSecurityController {
    private final OwnSecurityService service;
    private final VerifyEmailService verifyEmailService;
    private final PasswordRecoveryService passwordRecoveryService;

    /**
     * Constructor.
     *
     * @param service            - {@link OwnSecurityService} - service for security
     *                           logic.
     * @param verifyEmailService {@link VerifyEmailService} - service for email
     *                           verification.
     */
    @Autowired
    public OwnSecurityController(OwnSecurityService service,
        VerifyEmailService verifyEmailService,
        PasswordRecoveryService passwordRecoveryService) {
        this.service = service;
        this.verifyEmailService = verifyEmailService;
        this.passwordRecoveryService = passwordRecoveryService;
    }

    /**
     * Method for sign-up by our security logic.
     *
     * @param dto - {@link OwnSignUpDto} that have sign-up information.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Sign-up by own security logic")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = USER_CREATED, response = SuccessSignUpDto.class),
        @ApiResponse(code = 400, message = USER_ALREADY_REGISTERED_WITH_THIS_EMAIL)
    })
    @PostMapping("/signUp")
    @ApiLocale
    public ResponseEntity<SuccessSignUpDto> singUp(@Valid @RequestBody OwnSignUpDto dto,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.signUp(dto, locale.getLanguage()));
    }

    /**
     * Method for signing-up employee by our security logic.
     *
     * @param dto - {@link EmployeeSignUpDto} that have sign-up information for
     *            employee.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Sign-up employee by own security logic")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = USER_CREATED, response = SuccessSignUpDto.class),
        @ApiResponse(code = 400, message = USER_ALREADY_REGISTERED_WITH_THIS_EMAIL)
    })
    @PostMapping("/sign-up-employee")
    @ApiLocale
    public ResponseEntity<SuccessSignUpDto> singUpEmployee(@Valid @RequestBody EmployeeSignUpDto dto,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.signUpEmployee(dto, locale.getLanguage()));
    }

    /**
     * Method for sign-in by our security logic.
     *
     * @param dto - {@link OwnSignInDto} that have sign-in information.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Sign-in by own security logic")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = SuccessSignInDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping("/signIn")
    public SuccessSignInDto singIn(@Valid @RequestBody OwnSignInDto dto) {
        return service.signIn(dto);
    }

    /**
     * Method for verifying users email.
     *
     * @param token - {@link String} this is token (hash) to verify user.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Verify email by email token (hash that contains link for verification)")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN)
    })
    @GetMapping("/verifyEmail")
    public ResponseEntity<Boolean> verify(@RequestParam @NotBlank String token,
        @RequestParam("user_id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(verifyEmailService.verifyByToken(userId, token));
    }

    /**
     * Method for refresh access token.
     *
     * @param refreshToken - {@link String} this is refresh token.
     * @return {@link ResponseEntity} - with new access token.
     */
    @ApiOperation("Updating access token by refresh token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = REFRESH_TOKEN_NOT_VALID)
    })
    @GetMapping("/updateAccessToken")
    public ResponseEntity<Object> updateAccessToken(@RequestParam @NotBlank String refreshToken) {
        return ResponseEntity.ok().body(service.updateAccessTokens(refreshToken));
    }

    /**
     * Method for restoring password and sending email for restore.
     *
     * @param email - {@link String}
     * @return - {@link ResponseEntity}
     * @author Dmytro Dovhal
     */
    @ApiOperation("Sending email for restore password.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = USER_NOT_FOUND_BY_EMAIL)
    })
    @GetMapping("/restorePassword")
    @ApiLocale
    public ResponseEntity<Object> restore(@RequestParam @Email String email,
        @RequestParam Optional<String> ubs, @ApiIgnore @ValidLanguage Locale locale) {
        boolean isUbs = ubs.isPresent();
        log.info(Locale.getDefault().toString());
        passwordRecoveryService.sendPasswordRecoveryEmailTo(email, isUbs, locale.getLanguage());
        return ResponseEntity.ok().build();
    }

    /**
     * Method for changing password.
     *
     * @param form - {@link OwnRestoreDto}
     * @return - {@link ResponseEntity}
     * @author Dmytro Dovhal
     */
    @ApiOperation("Updating password for restore password option.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 404, message = TOKEN_FOR_RESTORE_IS_INVALID),
        @ApiResponse(code = 400, message = PASSWORD_DOES_NOT_MATCH)
    })
    @PostMapping("/updatePassword")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody OwnRestoreDto form) {
        passwordRecoveryService.updatePasswordUsingToken(form);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for updating current password.
     *
     * @param updateDto - {@link UpdatePasswordDto}
     * @return - {@link ResponseEntity}
     * @author Dmytro Dovhal
     */
    @ApiOperation("Updating current password.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PutMapping("/changePassword")
    public ResponseEntity<Object> updatePassword(@Valid @RequestBody UpdatePasswordDto updateDto,
        @ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        service.updateCurrentPassword(updateDto, email);
        return ResponseEntity.ok().build();
    }

    /**
     * Register new user from admin panel.
     *
     * @param userDto - dto with info for registering user.
     * @return - {@link UserAdminRegistrationDto}
     * @author Orest Mamchuk
     */
    @ApiOperation("Register new user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = UserAdminRegistrationDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PostMapping("/register")
    public ResponseEntity<UserAdminRegistrationDto> managementRegisterUser(
        @Valid @RequestBody UserManagementDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.managementRegisterUser(userDto));
    }

    /**
     * Method for checking if user has password.
     *
     * @return - {@link PasswordStatusDto}
     */
    @ApiOperation("Get password status for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/password-status")
    public ResponseEntity<PasswordStatusDto> passwordStatus(@ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok().body(new PasswordStatusDto(service.hasPassword(email)));
    }

    /**
     * Method for setting password for user that doesn't have one.
     *
     * @param dto {@link SetPasswordDto} password to be set.
     * @return {@link ResponseEntity}
     */
    @ApiOperation("Set password for user that doesn't have one.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/set-password")
    public ResponseEntity<Object> setPassword(@Valid @RequestBody SetPasswordDto dto,
        @ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        service.setPassword(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
