package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.GoogleApiException;
import greencity.exception.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleApiServiceTest {
    @InjectMocks
    GoogleApiService googleApiService;
    @Mock
    GeoApiContext context;
    @Mock
    GeocodingApiRequest request;

    @Test
    @SneakyThrows
    void testGetLocationByCoordinates() {
        String language = "uk";
        LatLng coordinates = new LatLng(20.000000, 20.000000);
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.latlng(coordinates)).thenReturn(request);
            when(request.language(language)).thenReturn(request);
            when(request.await()).thenReturn(new GeocodingResult[] {ModelUtils.getGeocodingResult().get(1)});

            assertDoesNotThrow(
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, language));
            verify(request).latlng(coordinates);
            verify(request).language(language);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void testGtLocationByCoordinatesThrowsNotFoundException() {
        String language = "uk";
        LatLng coordinates = new LatLng(20.000000, 20.000000);

        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(language)).thenReturn(request);
            when(request.latlng(coordinates)).thenReturn(request);
            when(request.await()).thenThrow(new InvalidRequestException("message"));
            String formattedCoordinates = String.format("%.8f,%.8f", coordinates.lat, coordinates.lng);
            NotFoundException exception =
                assertThrows(NotFoundException.class,
                    () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, language));

            assertEquals(ErrorMessage.NOT_FOUND_ADDRESS_BY_COORDINATES + formattedCoordinates, exception.getMessage());
            verify(request).language(language);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void testGetLocationByCoordinatesThrowsGoogleApiException() {
        String language = "uk";
        LatLng coordinates = new LatLng(20.000000, 20.000000);

        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(language)).thenReturn(request);
            when(request.await()).thenThrow(new GoogleApiException("something went wrong"));
            when(request.latlng(coordinates)).thenReturn(request);

            assertThrows(GoogleApiException.class,
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, language));
            verify(request).language(language);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }
}
