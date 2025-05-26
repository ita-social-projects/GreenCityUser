package greencity.service;

import greencity.client.GreenCityRemoteClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.socialnetwork.SocialNetworkImageRequestDTO;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkImageVO;
import greencity.entity.SocialNetworkImage;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.SocialNetworkImageRepo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import static greencity.ModelUtils.getSocialNetworkImage;
import static greencity.ModelUtils.getSocialNetworkImageId2;
import static greencity.ModelUtils.getSocialNetworkImageId3;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialNetworkImageServiceImplTest {
    @Mock
    SocialNetworkImageRepo socialNetworkImageRepo;

    @Mock
    ModelMapper modelMapper;

    @Mock
    GreenCityRemoteClient greenCityRemoteClient;

    @InjectMocks
    SocialNetworkImageServiceImpl socialNetworkImageService;

    private static final String URL_TO_CHECK = "HTTP://example.com/";
    private static final String DEFAULT_SOCIAL_NETWORK_IMAGE_HOST_PATH = "img/default_social_network_icon.png";
    private static final String DEFAULT_SOCIAL_NETWORK_IMAGE_PATH = "HTTP://img/default_social_network_icon.png/";

    @Test
    void getSocialNetworkImageByUrl() throws MalformedURLException {
        URL checkUrl = URI.create(URL_TO_CHECK).toURL();
        String host = checkUrl.getHost();
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath(checkUrl.getHost());
        socialNetworkImageVO.setImagePath(URL_TO_CHECK);

        Optional<SocialNetworkImage> socialNetworkImage = Optional.of(new SocialNetworkImage());
        when(socialNetworkImageRepo.findByHostPath(host))
            .thenReturn(socialNetworkImage);
        when(modelMapper.map(any(SocialNetworkImage.class), eq(SocialNetworkImageVO.class)))
            .thenReturn(socialNetworkImageVO);

        when(modelMapper.map(socialNetworkImageVO, SocialNetworkImageVO.class)).thenReturn(socialNetworkImageVO);

        assertEquals(socialNetworkImageVO, socialNetworkImageService.getSocialNetworkImageByUrl(URL_TO_CHECK));
    }

    @Test
    @SneakyThrows
    void getSocialNetworkImageByUrlReturnsDefaultImageTest() {
        URL checkUrl = URI.create(URL_TO_CHECK).toURL();
        String host = checkUrl.getHost();
        SocialNetworkImage defaultSocialNetworkImage = new SocialNetworkImage();
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath(checkUrl.getHost());
        socialNetworkImageVO.setImagePath(URL_TO_CHECK);

        when(socialNetworkImageRepo.findByHostPath(host))
            .thenThrow(new IllegalArgumentException());
        when(socialNetworkImageRepo.findByHostPath(AppConstant.DEFAULT_SOCIAL_NETWORK_IMAGE_HOST_PATH))
            .thenReturn(Optional.of(defaultSocialNetworkImage));

        when(modelMapper.map(defaultSocialNetworkImage, SocialNetworkImageVO.class)).thenReturn(socialNetworkImageVO);

        assertEquals(socialNetworkImageVO, socialNetworkImageService.getSocialNetworkImageByUrl(URL_TO_CHECK));
    }

    @Test
    void getSocialNetworkImageByUrlBadRequest() throws MalformedURLException {
        URL checkUrl = URI.create(URL_TO_CHECK).toURL();
        String host = checkUrl.getHost();
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath(host);
        socialNetworkImageVO.setImagePath(URL_TO_CHECK);

        when(socialNetworkImageRepo.findByHostPath(host)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> socialNetworkImageService.getSocialNetworkImageByUrl(URL_TO_CHECK));
    }

    @Test
    void findByHostPath() throws MalformedURLException {
        URL checkUrl = URI.create(URL_TO_CHECK).toURL();
        String host = checkUrl.getHost();
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath(host);
        socialNetworkImageVO.setImagePath(URL_TO_CHECK);

        Optional<SocialNetworkImage> socialNetworkImage = Optional.of(new SocialNetworkImage());
        when(socialNetworkImageRepo.findByHostPath(host))
            .thenReturn(socialNetworkImage);

        when(modelMapper.map(any(SocialNetworkImage.class), eq(SocialNetworkImageVO.class)))
            .thenReturn(socialNetworkImageVO);

        assertEquals(Optional.of(socialNetworkImageVO), socialNetworkImageService.findByHostPath(host));
    }

    @Test
    void getDefaultSocialNetworkImage() {
        SocialNetworkImageVO socialNetworkImageVO = new SocialNetworkImageVO();
        socialNetworkImageVO.setId(1L);
        socialNetworkImageVO.setHostPath(DEFAULT_SOCIAL_NETWORK_IMAGE_HOST_PATH);
        socialNetworkImageVO.setImagePath(DEFAULT_SOCIAL_NETWORK_IMAGE_PATH);

        Optional<SocialNetworkImage> socialNetworkImage = Optional.of(new SocialNetworkImage());
        when(socialNetworkImageRepo.findByHostPath(socialNetworkImageVO.getHostPath()))
            .thenReturn(socialNetworkImage);

        when(modelMapper.map(any(SocialNetworkImage.class), eq(SocialNetworkImageVO.class)))
            .thenReturn(socialNetworkImageVO);
        assertEquals(socialNetworkImageVO, socialNetworkImageService.getDefaultSocialNetworkImage());
    }

    @Test
    void testDelete() {
        Long idToDelete = 1L;
        SocialNetworkImage image = getSocialNetworkImage();
        List<String> filesToDelete = List.of(image.getImagePath());

        when(socialNetworkImageRepo.findById(idToDelete)).thenReturn(Optional.of(image));
        socialNetworkImageService.delete(idToDelete);

        verify(socialNetworkImageRepo).findById(idToDelete);
        verify(socialNetworkImageRepo).deleteById(idToDelete);
        verify(greenCityRemoteClient).deleteAllFiles(filesToDelete);
    }

    @Test
    void testDeleteImageNotFound() {
        Long idToDelete = 1L;
        when(socialNetworkImageRepo.findById(idToDelete)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> socialNetworkImageService.delete(idToDelete));
        verify(socialNetworkImageRepo).findById(idToDelete);
    }

    @Test
    void testDeleteAll() {
        List<Long> listIds = Arrays.asList(1L, 2L, 3L);
        SocialNetworkImage image1 = getSocialNetworkImage();
        SocialNetworkImage image2 = getSocialNetworkImageId2();
        SocialNetworkImage image3 = getSocialNetworkImageId3();
        List<SocialNetworkImage> images = List.of(image1, image2, image3);
        when(socialNetworkImageRepo.findAllById(listIds)).thenReturn(images);
        socialNetworkImageService.deleteAll(listIds);

        verify(socialNetworkImageRepo).findAllById(listIds);
        verify(socialNetworkImageRepo).deleteById(listIds.get(0));
        verify(socialNetworkImageRepo).deleteById(listIds.get(1));
        verify(socialNetworkImageRepo).deleteById(listIds.get(2));

        verify(greenCityRemoteClient)
            .deleteAllFiles(List.of(image1.getImagePath(), image2.getImagePath(), image3.getImagePath()));
    }

    @Test
    void testSave() {
        SocialNetworkImageRequestDTO requestDTO = new SocialNetworkImageRequestDTO();
        MultipartFile mockImage = mock(MultipartFile.class);
        SocialNetworkImage savedEntity = new SocialNetworkImage();
        SocialNetworkImageResponseDTO expectedResponseDTO = new SocialNetworkImageResponseDTO();

        when(greenCityRemoteClient.uploadFile(any(MultipartFile.class))).thenReturn("mockImagePath");
        when(modelMapper.map(requestDTO, SocialNetworkImage.class)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, SocialNetworkImageResponseDTO.class)).thenReturn(expectedResponseDTO);
        when(socialNetworkImageRepo.save(savedEntity)).thenReturn(savedEntity);

        SocialNetworkImageResponseDTO result = socialNetworkImageService.save(requestDTO, mockImage);

        assertNotNull(result);
        assertEquals(expectedResponseDTO, result);
        verify(greenCityRemoteClient).uploadFile(mockImage);
        verify(socialNetworkImageRepo).save(savedEntity);
        verify(modelMapper).map(requestDTO, SocialNetworkImage.class);
        verify(modelMapper).map(savedEntity, SocialNetworkImageResponseDTO.class);
    }

    @Test
    void saveTestWhenDataIntegrityViolationIsThrownShouldThrowNotSavedException() {
        SocialNetworkImageRequestDTO requestDTO = new SocialNetworkImageRequestDTO();
        MultipartFile mockImage = mock(MultipartFile.class);
        SocialNetworkImage savedEntity = new SocialNetworkImage();

        when(greenCityRemoteClient.uploadFile(any(MultipartFile.class))).thenReturn("mockImagePath");
        when(modelMapper.map(requestDTO, SocialNetworkImage.class)).thenReturn(savedEntity);
        when(socialNetworkImageRepo.save(savedEntity)).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(
            NotSavedException.class,
            () -> socialNetworkImageService.save(requestDTO, mockImage));

        verify(greenCityRemoteClient).uploadFile(mockImage);
        verify(socialNetworkImageRepo).save(savedEntity);
        verify(modelMapper).map(requestDTO, SocialNetworkImage.class);
        verify(modelMapper, never()).map(savedEntity, SocialNetworkImageResponseDTO.class);
    }

    @Test
    void testFindAll() {
        Pageable pageable = mock(Pageable.class);
        List<SocialNetworkImage> mockImages = Collections.singletonList(new SocialNetworkImage());
        List<SocialNetworkImageResponseDTO> expectedDTOs =
            Collections.singletonList(new SocialNetworkImageResponseDTO());
        Page<SocialNetworkImage> mockPage = new PageImpl<>(mockImages, pageable, 1);

        when(socialNetworkImageRepo.findAll(pageable)).thenReturn(mockPage);
        when(modelMapper.map(any(SocialNetworkImage.class), eq(SocialNetworkImageResponseDTO.class)))
            .thenReturn(expectedDTOs.get(0));

        PageableDto<SocialNetworkImageResponseDTO> result = socialNetworkImageService.findAll(pageable);

        assertEquals(expectedDTOs.get(0), result.getPage().get(0));
        assertEquals(mockPage.getTotalElements(), result.getTotalElements());
        assertEquals(mockPage.getTotalPages(), result.getTotalPages());

        verify(socialNetworkImageRepo).findAll(pageable);
        verify(modelMapper, times(mockImages.size()))
            .map(any(SocialNetworkImage.class), eq(SocialNetworkImageResponseDTO.class));
    }

    @Test
    void findDtoByIdTest() {
        Long id = 1L;
        String hostPath = "example.com";
        String imagePath = "path/to/image.png";
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage(id, imagePath, hostPath);
        SocialNetworkImageResponseDTO expectedResult = new SocialNetworkImageResponseDTO(id, imagePath, hostPath);

        when(socialNetworkImageRepo.findById(id))
            .thenReturn(Optional.of(socialNetworkImage));
        when(modelMapper.map(socialNetworkImage, SocialNetworkImageResponseDTO.class))
            .thenReturn(expectedResult);

        SocialNetworkImageResponseDTO actualResult = socialNetworkImageService.findDtoById(id);

        assertEquals(expectedResult, actualResult);
        verify(socialNetworkImageRepo).findById(id);
        verify(modelMapper).map(socialNetworkImage, SocialNetworkImageResponseDTO.class);
    }

    @Test
    void findDtoByIdWhenNotFoundTest() {
        Long nonExistentId = 999L;
        String expectedExceptionMessage = ErrorMessage.SOCIAL_NETWORK_IMAGE_FOUND_BY_ID + nonExistentId;

        when(socialNetworkImageRepo.findById(nonExistentId))
            .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> socialNetworkImageService.findDtoById(nonExistentId));

        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(socialNetworkImageRepo).findById(nonExistentId);
        verify(modelMapper, never()).map(any(SocialNetworkImage.class), eq(SocialNetworkImageResponseDTO.class));
    }

    @Test
    void updateTest() {
        Long id = 1L;
        String hostPath = "updated-example.com";
        String imagePath = "new/image/path.png";
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage();
        socialNetworkImage.setId(id);
        SocialNetworkImageResponseDTO requestDTO = new SocialNetworkImageResponseDTO(id, imagePath, hostPath);
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(socialNetworkImageRepo.findById(id))
            .thenReturn(Optional.of(socialNetworkImage));
        when(greenCityRemoteClient.uploadFile(multipartFile))
            .thenReturn(imagePath);
        when(socialNetworkImageRepo.save(socialNetworkImage))
            .thenReturn(socialNetworkImage);

        socialNetworkImageService.update(requestDTO, multipartFile);

        verify(socialNetworkImageRepo).findById(id);
        verify(greenCityRemoteClient).uploadFile(multipartFile);
        verify(socialNetworkImageRepo).save(socialNetworkImage);
    }

    @Test
    void updateWhenImageIsNullTest() {
        Long id = 1L;
        String hostPath = "updated-example.com";
        String imagePath = "new/image/path.png";
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage();
        socialNetworkImage.setId(id);
        SocialNetworkImageResponseDTO requestDTO = new SocialNetworkImageResponseDTO(id, imagePath, hostPath);
        MultipartFile multipartFile = null;

        when(socialNetworkImageRepo.findById(id))
            .thenReturn(Optional.of(socialNetworkImage));
        when(socialNetworkImageRepo.save(socialNetworkImage))
            .thenReturn(socialNetworkImage);

        socialNetworkImageService.update(requestDTO, multipartFile);

        verify(socialNetworkImageRepo).findById(id);
        verify(greenCityRemoteClient, never()).uploadFile(multipartFile);
        verify(socialNetworkImageRepo).save(socialNetworkImage);
    }

    @Test
    void updateWhenSocialNetworkImageNotFoundTest() {
        Long nonExistentId = 999L;
        SocialNetworkImageResponseDTO requestDTO = new SocialNetworkImageResponseDTO();
        requestDTO.setId(nonExistentId);
        String expectedExceptionMessage = ErrorMessage.SOCIAL_NETWORK_IMAGE_FOUND_BY_ID + nonExistentId;

        MultipartFile mockImage = mock(MultipartFile.class);

        when(socialNetworkImageRepo.findById(nonExistentId))
            .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> socialNetworkImageService.update(requestDTO, mockImage));

        assertEquals(expectedExceptionMessage, exception.getMessage());
        verify(socialNetworkImageRepo).findById(nonExistentId);
        verify(greenCityRemoteClient, never()).uploadFile(any(MultipartFile.class));
        verify(socialNetworkImageRepo, never()).save(any(SocialNetworkImage.class));
    }
}
