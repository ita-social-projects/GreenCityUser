package greencity.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import greencity.properties.AzureProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AzureBlobClientFactoryTest {
    @Mock
    private AzureProperties azureProperties;

    @Test
    void getContainerClient_doesNotThrow() {
        AzureBlobClientFactory factory = new AzureBlobClientFactory(azureProperties);

        when(azureProperties.getAzureConnectionString()).thenReturn(
            "DefaultEndpointsProtocol=https;AccountName=fake;AccountKey=fakeKey;EndpointSuffix=core.windows.net");
        when(azureProperties.getAzureContainerName()).thenReturn("test-container");

        assertDoesNotThrow(factory::getContainerClient);
    }
}
