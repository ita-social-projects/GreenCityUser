package greencity.constant;

public final class LogMessage {
    public static final String IN_SEND_CHANGE_PLACE_STATUS_EMAIL = "in sendChangePlaceStatusEmail(), place: {}";
    public static final String IN_FIND_ID_BY_EMAIL = "in findIdByEmail(), email: {}";
    public static final String IN_SEND_EMAIL = "in sendEmail(), receiver: {}, subject: {}";
    public static final String IN_CREATE_TEMPLATE_NAME = "in createEmailTemplate(), vars: {}, templateName: {}";
    public static final String IN_SEND_ADDED_NEW_PLACES_REPORT_EMAIL =
        "in sendAddedNewPlacesReportEmail(), subscribers: {}, categories: {}, notificationType: {}";
    public static final String IN_FIND_UUID_BY_EMAIL = "in findUUIDByEmail(), email: {}";

    private LogMessage() {
    }
}
