package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.GoogleApiException;
import greencity.exception.exceptions.NotFoundException;
import lombok.SneakyThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleApiServiceTest {
    @InjectMocks
    GoogleApiService googleApiService;
    @Mock
    GeoApiContext context;
    @Mock
    GeocodingApiRequest request;
    private final AddressType[] addressTypes =
        {AddressType.LOCALITY, AddressType.ADMINISTRATIVE_AREA_LEVEL_1, AddressType.COUNTRY};
    private final String languageUa = "uk";
    private final LatLng coordinates = new LatLng(20.000000, 20.000000);

    @Test
    @SneakyThrows
    void getLocationByCoordinatesTest() {
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.latlng(coordinates)).thenReturn(request);
            when(request.language(languageUa)).thenReturn(request);
            when(request.resultType(addressTypes)).thenReturn(request);
            when(request.await()).thenReturn(ModelUtils.getGeocodingResult().toArray(GeocodingResult[]::new));
            assertDoesNotThrow(
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, languageUa,
                    addressTypes));
            verify(request).latlng(coordinates);
            verify(request).language(languageUa);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void getLocationByCoordinatesThrowsNotFoundExceptionTest() {
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(languageUa)).thenReturn(request);
            when(request.latlng(coordinates)).thenReturn(request);
            when(request.resultType(addressTypes)).thenReturn(request);
            when(request.await()).thenThrow(new InvalidRequestException("message"));
            String formattedCoordinates = "%.8f,%.8f".formatted(coordinates.lat, coordinates.lng);
            NotFoundException exception =
                assertThrows(NotFoundException.class,
                    () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, languageUa,
                        addressTypes));

            assertEquals(ErrorMessage.NOT_FOUND_ADDRESS_BY_COORDINATES + formattedCoordinates, exception.getMessage());
            verify(request).language(languageUa);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void getLocationByCoordinatesThrowsGoogleApiExceptionTest() {
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(languageUa)).thenReturn(request);
            when(request.resultType(addressTypes)).thenReturn(request);
            when(request.await()).thenThrow(new GoogleApiException("something went wrong"));
            when(request.latlng(coordinates)).thenReturn(request);

            assertThrows(GoogleApiException.class,
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, languageUa,
                    addressTypes));
            verify(request).language(languageUa);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void getLocationByCoordinatesThrowsInterruptedExceptionTest() {
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(languageUa)).thenReturn(request);
            when(request.resultType(addressTypes)).thenReturn(request);
            when(request.await()).thenThrow(new InterruptedException());
            when(request.latlng(coordinates)).thenReturn(request);

            assertThrows(GoogleApiException.class,
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, languageUa,
                    addressTypes));
            verify(request).language(languageUa);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }
}
