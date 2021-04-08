package greencity.constant;

public final class SwaggerExampleModel {
    private static final String BEFORE_EXAMPLE = "<div>\n"
        + "\t<ul class=\"tab\">\n"
        + "\t\t<li class=\"tabitem active\">\n"
        + "\t\t\t<a class=\"tablinks\" data-name=\"example\">Example Value</a>\n"
        + "\t\t</li>\n"
        + "\t\t<li class=\"tabitem\">\n"
        + "\t\t\t<a class=\"tablinks\" data-name=\"model\">Model</a>\n"
        + "\t\t</li>\n"
        + "\t</ul>\n"
        + "\t<pre>\n";

    private static final String AFTER_EXAMPLE = "\t</pre>\n"
        + "</div>";
    private static final String EXAMPLE = "  \"id\": \"0\",\n"
        + "  \"name\": \"string\",\n"
        + "  \"profilePicturePath\": \"string\"\n";

    public static final String USER_PROFILE_PICTURE_DTO =
        "User Profile Picture\n"
            + BEFORE_EXAMPLE
            + "{\n"
            + EXAMPLE
            + "}\n"
            + AFTER_EXAMPLE;

    private SwaggerExampleModel() {
    }
}
