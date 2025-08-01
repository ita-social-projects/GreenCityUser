package greencity.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.service.FileService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {
    @InjectMocks
    FileController fileController;

    @Mock
    FileService fileService;
    String baseUrl = "/files";
    ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(fileController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void uploadAllTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "image", MediaType.IMAGE_JPEG_VALUE, new byte[1]);

        mockMvc.perform(multipart(baseUrl)
            .file(file))
            .andExpect(status().isOk());

        verify(fileService).upload(List.of(file));
    }

    @Test
    void uploadTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image", MediaType.IMAGE_JPEG_VALUE, new byte[1]);

        mockMvc.perform(multipart(baseUrl + "/single")
            .file(file))
            .andExpect(status().isOk());

        verify(fileService).upload(file);
    }

    @Test
    void deleteAllTest() throws Exception {
        List<String> paths = List.of("path1", "path2");
        String pathsJson = objectMapper.writeValueAsString(paths);

        mockMvc.perform(delete(baseUrl)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(pathsJson))
            .andExpect(status().isOk());

        verify(fileService).deleteAll(paths);
    }

    @Test
    void deleteTest() throws Exception {
        String path = "path1";

        mockMvc.perform(delete(baseUrl + "/single")
            .param("path", path))
            .andExpect(status().isOk());

        verify(fileService).delete(path);
    }
}
