package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.GoogleApiException;
import greencity.exception.exceptions.NotFoundException;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
@Data
public class GoogleApiService {
    private final GeoApiContext context;
    private static final List<Locale> locales = List.of(new Locale("uk"), new Locale("en"));

    /**
     * Method gets user location by coordinates.
     * 
     * @param latitude  user's latitude
     * @param longitude user's longitude
     * @return {@link greencity.entity.UserLocation}
     */
    public GeocodingResult getLocationByCoordinates(Double latitude, Double longitude, Integer langCode) {
        try {
            GeocodingResult[] results = GeocodingApi.newRequest(context).latlng(new LatLng(latitude, longitude))
                .language(locales.get(langCode).getLanguage()).await();
            return results[0];
        } catch (IOException | InterruptedException | ApiException e) {
            Thread.currentThread().interrupt();
            if (e instanceof InvalidRequestException) {
                String formattedCoords = String.format("%.8f,%.8f", latitude, longitude);
                throw new NotFoundException(ErrorMessage.NOT_FOUND_ADDRESS_BY_COORDINATES + formattedCoords);
            }
            throw new GoogleApiException(e.getMessage());
        }
    }
}