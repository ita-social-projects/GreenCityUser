package greencity.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class AzureCloudStorageServiceTest {
    @InjectMocks
    AzureCloudStorageService azureCloudStorageService;

    @Mock
    private ModelMapper modelMapper;
}
