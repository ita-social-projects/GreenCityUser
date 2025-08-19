package greencity.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AzureBlobClientFactory {
    @Value("${azure.connection.string}")
    private String connectionString;
    @Value("${azure.container.name}")
    private String containerName;

    public BlobContainerClient getContainerClient() {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString).buildClient();
        return serviceClient.getBlobContainerClient(containerName);
    }
}
