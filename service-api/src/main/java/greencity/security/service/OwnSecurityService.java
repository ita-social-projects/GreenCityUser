package greencity.security.service;

import greencity.dto.user.UserAdminRegistrationDto;
import greencity.dto.user.UserManagementCreateDto;
import greencity.enums.ProjectName;
import greencity.exception.exceptions.UserProfileCreationException;
import greencity.security.dto.AccessRefreshTokensDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import greencity.security.dto.ownsecurity.EmployeeSignUpDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.SetPasswordDto;
import greencity.security.dto.ownsecurity.TestersSignInRequest;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;

/**
 * Provides the interface to manage {@link OwnSecurityService} entity.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
public interface OwnSecurityService {
    /**
     * Method that allow you sign-up user.
     *
     * @param dto a value of {@link OwnSignUpDto}
     * @return {@link SuccessSignUpDto}
     * @author Yurii Koval
     */
    SuccessSignUpDto signUp(OwnSignUpDto dto, String language);

    /**
     * Method that allows you sign-up employee.
     *
     * @param dto a value of {@link EmployeeSignUpDto}
     * @return {@link SuccessSignUpDto}
     */
    SuccessSignUpDto signUpEmployee(EmployeeSignUpDto dto, String language);

    /**
     * Method that allows you to create user UBS and GreenCity profiles.
     *
     * @param userId a value of {@link Long}
     * @throws UserProfileCreationException if user external profile wasn't created
     */
    void createUserProfiles(Long userId);

    /**
     * Method that allow you sign-in user.
     *
     * @param dto a value of {@link OwnSignInDto}
     * @return {@link SuccessSignInDto}
     */
    SuccessSignInDto signIn(OwnSignInDto dto);

    /**
     * Method that update your access token by refresh token.
     *
     * @param refreshToken a value of {@link String}
     * @param projectName  service from where this method is called
     * @return {@link AccessRefreshTokensDto} this is DTO with new access token
     */
    AccessRefreshTokensDto updateAccessTokens(String refreshToken, ProjectName projectName);

    /**
     * Method for updating current password.
     *
     * @param updatePasswordDto {@link UpdatePasswordDto}
     * @param email             {@link String} - user email.
     * @author Dmytro Dovhal
     */
    void updateCurrentPassword(UpdatePasswordDto updatePasswordDto, String email);

    /**
     * Method for registering a user from admin panel.
     *
     * @param dto a value of {@link UserManagementCreateDto}
     * @author Vasyl Zhovnir
     */
    UserAdminRegistrationDto managementRegisterUser(UserManagementCreateDto dto);

    /**
     * Checks if user has password.
     *
     * @param email {@link String} email of user.
     * @return {@link Boolean}
     */
    boolean hasPassword(String email);

    /**
     * Sets password for user that doesn't have one.
     *
     * @param dto   {@link SetPasswordDto} password to be set.
     * @param email {@link String} email of user.
     */
    void setPassword(SetPasswordDto dto, String email);

    /**
     * Unblocks user account by provided token.
     *
     * @param token {@link String} token for unblocking user account.
     */
    void unblockAccount(String token);

    /**
     * Allows testers to sign in without captcha token using their credentials.
     *
     * @param request a {@link TestersSignInRequest} containing sign-in information
     *                for testers.
     * @return {@link SuccessSignInDto} containing sign-in success details, such as
     *         access and refresh tokens.
     */
    SuccessSignInDto testersSignIn(TestersSignInRequest request);
}
