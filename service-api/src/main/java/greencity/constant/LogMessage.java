package greencity.constant;

public final class LogMessage {
    public static final String IN_FIND_ID_BY_EMAIL = "in findIdByEmail(), email: {}";
    public static final String IN_SEND_EMAIL = "in sendEmail(), receiver: {}, subject: {}";
    public static final String IN_CREATE_TEMPLATE_NAME = "in createEmailTemplate(), vars: {}, templateName: {}";

    private LogMessage() {
    }
}
