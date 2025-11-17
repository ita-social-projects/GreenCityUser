package greencity.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import greencity.properties.AzureProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AzureBlobClientFactory {
    private final AzureProperties azureProperties;

    public BlobContainerClient getContainerClient() {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
            .connectionString(azureProperties.getAzureConnectionString()).buildClient();
        return serviceClient.getBlobContainerClient(azureProperties.getAzureContainerName());
    }
}
