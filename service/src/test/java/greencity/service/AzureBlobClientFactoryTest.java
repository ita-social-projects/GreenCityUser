package greencity.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AzureBlobClientFactoryTest {
    @Test
    void getContainerClient_doesNotThrow() {
        AzureBlobClientFactory factory = new AzureBlobClientFactory();

        ReflectionTestUtils.setField(factory, "connectionString",
            "DefaultEndpointsProtocol=https;AccountName=fake;AccountKey=fakeKey;EndpointSuffix=core.windows.net");
        ReflectionTestUtils.setField(factory, "containerName", "test-container");

        assertDoesNotThrow(factory::getContainerClient);
    }
}
