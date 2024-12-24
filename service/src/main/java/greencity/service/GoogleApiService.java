package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.GoogleApiException;
import greencity.exception.exceptions.NotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleApiService {
    private final GeoApiContext context;

    /**
     * Method gets user location by coordinates.
     *
     * @param latitude    user's latitude
     * @param longitude   user's longitude
     * @param addressTypes preferred result_types that should be included in the
     *                    result.
     * @return {@link GeocodingResult}
     */
    public GeocodingResult getLocationByCoordinates(Double latitude, Double longitude, String lang,
        AddressType[] addressTypes) {
        try {
            return Arrays.stream(GeocodingApi.newRequest(context).latlng(new LatLng(latitude, longitude))
                .language(lang)
                .resultType(addressTypes)
                .await())
                .max(Comparator.comparingInt(a -> a.addressComponents.length))
                .orElseThrow(() -> new GoogleApiException("Geocoding result was not found"));
        } catch (InvalidRequestException e) {
            String formattedCoords = "%.8f,%.8f".formatted(latitude, longitude);
            throw new NotFoundException(ErrorMessage.NOT_FOUND_ADDRESS_BY_COORDINATES + formattedCoords);
        } catch (IOException | InterruptedException | ApiException e) {
            Thread.currentThread().interrupt();
            throw new GoogleApiException(e.getMessage());
        }
    }
}