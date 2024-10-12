package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessage {
    public static final String USER_NOT_FOUND_BY_ID = "The user does not exist by this id: ";
    public static final String USER_ID_IS_NULL = "User id is null";
    public static final String USER_NOT_FOUND_BY_UUID = "The user does not exist by this uuid: ";
    public static final String USER_NOT_FOUND_BY_EMAIL = "The user does not exist by this email: ";
    public static final String USER_CANT_UPDATE_THEMSELVES = "User can't update themselves";
    public static final String IMPOSSIBLE_UPDATE_USER_STATUS = "Impossible to update status of admin or moderator";
    public static final String IMAGE_EXISTS = "Image should be download, PNG or JPEG ";
    public static final String USER_CANNOT_ADD_MORE_THAN_5_SOCIAL_NETWORK_LINKS =
        "User cannot add more than 5 social network links";
    public static final String INVALID_URI = "The string could not be parsed as a URI reference.";
    public static final String MALFORMED_URL = "Malformed URL. The string could not be parsed.";
    public static final String USER_CANNOT_ADD_SAME_SOCIAL_NETWORK_LINKS =
        "User cannot add the same social network links";
    public static final String USER_DEACTIVATED = "User is deactivated";
    public static final String BAD_GOOGLE_TOKEN = "Bad google token. ";
    public static final String EXPIRED_GOOGLE_ID_TOKEN = "Google id token is not valid or expired. ";
    public static final String TRANSACTION_FAILED = "Transaction failed and rolled back due to restClient failure. ";
    public static final String BAD_FACEBOOK_TOKEN = "Bad facebook token";
    public static final String NO_EMAIL_FOUND_FOR_VERIFICATION_WITH_THIS_TOKEN =
        "No email found for verification with this token";
    public static final String EMAIL_TOKEN_EXPIRED = "User late with verify. Token is invalid.";
    public static final String PASSWORD_RESTORE_LINK_ALREADY_SENT =
        "Password restore link already sent, please check your email: ";
    public static final String REFRESH_TOKEN_NOT_VALID = "Refresh token not valid!";
    public static final String BAD_PASSWORD = "Bad password";
    public static final String USER_ALREADY_REGISTERED_WITH_THIS_EMAIL = "User with this email is already registered";
    public static final String PASSWORDS_DO_NOT_MATCH = "The passwords don't match";
    public static final String PASSWORD_DOES_NOT_MATCH = "The password doesn't match";
    public static final String SELECT_CORRECT_LANGUAGE = "Select correct language: 'en' or 'ua'";
    public static final String TOKEN_FOR_RESTORE_IS_INVALID = "Token is null or it doesn't exist.";
    public static final String LANGUAGE_NOT_FOUND_BY_ID = "The language does not exist by this id: ";
    public static final String USER_DEACTIVATION_REASON_IS_EMPTY = "The User deactivation reasons list is empty";
    public static final String USER_ALREADY_HAS_PASSWORD = "User already has password";
    public static final String USER_HAS_NO_PERMISSION = "Current user has no permission for this action";
    public static final String LINK_IS_NO_ACTIVE = "This link is no longer active";
    public static final String USER_BLOCKED = "User is blocked";
    public static final String USER_CREATED = "User is not activated";
    public static final String USER_DELETED = "User deleted";
    public static final String USER_EMAIL_IS_NOT_VERIFIED = "The user's email address has not been verified.";
    public static final String NOT_FOUND_ADDRESS_BY_COORDINATES = "Not found address with such coordinates: ";
    public static final String USER_DID_NOT_SET_ANY_CITY = "User did not set any city";
    public static final String NOT_VALID_BOOLEAN_VALUE = "%s is not a valid Boolean value for %s";
    public static final String INSUFFICIENT_LOCATION_DATA_FOUND =
        "Insufficient location data found for the provided coordinates.";
    public static final String USER_CANNOT_DEACTIVATE_YOURSELF = "You cannot deactivate yourself if you are not a USER";
    public static final String USER_CANNOT_DEACTIVATE_OTHERS = "USER cannot deactivate other users";
    public static final String ADMIN_CANNOT_DEACTIVATE_OTHER_ADMIN = "ADMIN cannot deactivate other ADMIN users";
    public static final String YOU_DO_NOT_HAVE_PERMISSIONS_TO_DEACTIVATE_THIS_USER =
        "You do not have permission to deactivate this user";
    public static final String BASE64_DECODE_MESSAGE = "Can't decode from base64 format";
    public static final String BRUTEFORCE_PROTECTION_MESSAGE =
        "User account is blocked due to too many failed login attempts. Try again in %s hour(s)";
    public static final String COULD_NOT_RETRIEVE_CHECKOUT_RESPONSE = "Could not retrieve checkout response";
    public static final String WRONG_CAPTCHA = "Wrong captcha";
    public static final String BRUTEFORCE_PROTECTION_MESSAGE_WRONG_PASSWORD =
        "User account is blocked due to too many failed login attempts. Try again in %s minutes";
}
