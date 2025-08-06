package greencity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.ImageUrlParseException;
import greencity.exception.exceptions.NotSavedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class AzureCloudStorageServiceTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AzureBlobClientFactory blobClientFactory;
    @Mock
    private BlobContainerClient blobContainerClient;

    @Mock
    private BlobClient blobClient;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AzureCloudStorageService azureCloudStorageService;

    @Test
    void convertToMultipartImageThrowsBadRequestException() {
        when(modelMapper.map("Image", MultipartFile.class)).thenThrow(new BadRequestException("S"));
        assertThrows(BadRequestException.class, () -> azureCloudStorageService.convertToMultipartImage("Image"));
    }

    @Test
    void convertToMultipartImage() {
        MultipartFile multipartFile1 = new MockMultipartFile("Image", "Image".getBytes(StandardCharsets.UTF_8));
        when(modelMapper.map("Image", MultipartFile.class)).thenReturn(multipartFile1);
        assertEquals(multipartFile1, azureCloudStorageService.convertToMultipartImage("Image"));
    }

    @Test
    void upload_singleFile_success() throws IOException {
        when(blobClientFactory.getContainerClient()).thenReturn(blobContainerClient);
        when(multipartFile.getOriginalFilename()).thenReturn("file.jpg");
        when(multipartFile.getSize()).thenReturn(5L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        when(blobContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn("https://mock.azure/container/file.jpg");

        String result = azureCloudStorageService.upload(multipartFile);

        assertEquals("https://mock.azure/container/file.jpg", result);
        verify(blobClient).upload(any(InputStream.class), eq(5L));
    }

    @Test
    void upload_singleFile_throwsNotSavedException() throws IOException {
        when(blobClientFactory.getContainerClient()).thenReturn(blobContainerClient);
        when(multipartFile.getOriginalFilename()).thenReturn("file.jpg");
        when(multipartFile.getInputStream()).thenThrow(new IOException("error"));
        when(blobContainerClient.getBlobClient(anyString())).thenReturn(blobClient);

        assertThrows(NotSavedException.class, () -> azureCloudStorageService.upload(multipartFile));
    }

    @Test
    void delete_existingBlob_deletesSuccessfully() {
        when(blobClientFactory.getContainerClient()).thenReturn(blobContainerClient);
        String url = "https://mock.azure/container/file.jpg";
        when(blobContainerClient.getBlobClient("file.jpg")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(true);

        azureCloudStorageService.delete(url);

        verify(blobClient).delete();
    }

    @Test
    void delete_invalidUrl_throwsImageUrlParseException() {
        String badUrl = ":::://not-a-valid-uri";
        assertThrows(ImageUrlParseException.class, () -> azureCloudStorageService.delete(badUrl));
    }

    @Test
    void deleteAll_deletesEachFile() {
        when(blobClientFactory.getContainerClient()).thenReturn(blobContainerClient);
        String url1 = "https://mock.azure/container/file1.jpg";
        String url2 = "https://mock.azure/container/file2.jpg";

        BlobClient blobClient1 = mock(BlobClient.class);
        BlobClient blobClient2 = mock(BlobClient.class);

        when(blobContainerClient.getBlobClient("file1.jpg")).thenReturn(blobClient1);
        when(blobContainerClient.getBlobClient("file2.jpg")).thenReturn(blobClient2);

        when(blobClient1.exists()).thenReturn(true);
        when(blobClient2.exists()).thenReturn(true);

        azureCloudStorageService.deleteAll(List.of(url1, url2));

        verify(blobClient1).delete();
        verify(blobClient2).delete();
    }
}
