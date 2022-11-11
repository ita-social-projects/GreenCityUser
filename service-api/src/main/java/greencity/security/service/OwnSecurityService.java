package greencity.security.service;

import greencity.dto.user.UserAdminRegistrationDto;
import greencity.dto.user.UserManagementDto;
import greencity.security.dto.AccessRefreshTokensDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import greencity.security.dto.ownsecurity.*;

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
     * @author Inna Yashna
     */
    SuccessSignUpDto signUpEmployee(EmployeeSignUpDto dto, String language);

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
     * @return {@link AccessRefreshTokensDto} this is DTO with new access token
     */
    AccessRefreshTokensDto updateAccessTokens(String refreshToken);

    /**
     * Method for updating password.
     *
     * @param pass {@link String}
     * @param id   {@link Long}
     */
    void updatePassword(String pass, Long id);

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
     * @param dto a value of {@link UserManagementDto}
     * @author Vasyl Zhovnir
     */
    UserAdminRegistrationDto managementRegisterUser(UserManagementDto dto);

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
}
