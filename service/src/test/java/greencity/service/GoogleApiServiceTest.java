package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleApiServiceTest {
    @InjectMocks
    GoogleApiService googleApiService;
    @Mock
    GeoApiContext context;
    @Mock
    GeocodingApiRequest request;

    @Test
    @SneakyThrows
    void testGetLocationByCoordinates() {
        Integer langCode = 0;
        String language = "uk";
        LatLng coordinates = new LatLng(20.000000, 20.000000);
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.latlng(coordinates)).thenReturn(request);
            when(request.language(language)).thenReturn(request);
            when(request.await()).thenReturn(new GeocodingResult[1]);

            assertDoesNotThrow(
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, langCode));
            verify(request).latlng(coordinates);
            verify(request).language(language);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void testGtLocationByCoordinatesThrowsNotFoundException() {
        Integer langCode = 0;
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
                    () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, langCode));

            assertEquals(ErrorMessage.NOT_FOUND_ADDRESS_BY_COORDINATES + formattedCoordinates, exception.getMessage());
            verify(request).language(language);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void testGetLocationByCoordinatesThrowsGoogleApiException() {
        Integer langCode = 0;
        String language = "uk";
        LatLng coordinates = new LatLng(20.000000, 20.000000);

        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(language)).thenReturn(request);
            when(request.await()).thenThrow(new InterruptedException());
            when(request.latlng(coordinates)).thenReturn(request);

            assertThrows(GoogleApiException.class,
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, langCode));
            verify(request).language(language);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }
}