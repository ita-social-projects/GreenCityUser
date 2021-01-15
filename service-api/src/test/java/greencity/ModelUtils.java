package greencity;

import java.net.MalformedURLException;
import java.net.URL;

public class ModelUtils {

    public static URL getUrl() throws MalformedURLException {
        return new URL(TestConst.SITE);
    }
}
