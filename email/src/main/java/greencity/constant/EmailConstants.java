package greencity.constant;

public final class EmailConstants {
    public static final String EMAIL_CONTENT_TYPE = "text/html; charset=utf-8";
    // subjects
    public static final String VERIFY_EMAIL = "Verify your email address";
    public static final String CONFIRM_RESTORING_PASS = "Confirm restoring password";
    public static final String APPROVE_REGISTRATION_SUBJECT = "Approve your registration";
    // params
    public static final String CLIENT_LINK = "clientLink";
    public static final String USER_NAME = "name";
    public static final String VERIFY_ADDRESS = "verifyAddress";
    public static final String RESTORE_PASS = "restorePassword";
    public static final String APPROVE_REGISTRATION = "approveRegistration";
    // templates
    public static final String VERIFY_EMAIL_PAGE = "verify-email-page";
    public static final String RESTORE_EMAIL_PAGE = "restore-email-page";
    public static final String USER_APPROVAL_EMAIL_PAGE = "user-approval-email-page";

    private EmailConstants() {
    }
}
