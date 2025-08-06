package greencity.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.retryabletask.RetryableTaskVO;
import greencity.enums.RetryableTaskType;
import greencity.exception.exceptions.TaskProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractRetryableTaskProcessorTest {
    private ObjectMapper objectMapper;
    private TestRetryableProcessor testProcessor;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testProcessor = new TestRetryableProcessor(objectMapper);
    }

    @Test
    void getRetryableTaskType() {

    }

    @Test
    void process_shouldCallHandleWithCorrectPayload() {
        TestDto payload = new TestDto("Test");
        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        RetryableTaskVO taskVO = new RetryableTaskVO();
        taskVO.setPayload(jsonPayload);

        testProcessor.process(taskVO);

        Assertions.assertEquals("Test", testProcessor.lastHandled.getName());
    }

    @Test
    void process_shouldThrowTaskProcessingException_onInvalidJson() {
        RetryableTaskVO taskVO = new RetryableTaskVO();
        taskVO.setPayload("{invalid-json}");

        TaskProcessingException ex = Assertions.assertThrows(TaskProcessingException.class,
            () -> testProcessor.process(taskVO));

        Assertions.assertTrue(ex.getMessage().contains("Failed to process retryable task"));
    }

    private static class TestDto {
        private String name;

        public TestDto() {
        }

        public TestDto(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class TestRetryableProcessor extends AbstractRetryableTaskProcessor<TestDto> {

        TestDto lastHandled;

        protected TestRetryableProcessor(ObjectMapper objectMapper) {
            super(objectMapper, RetryableTaskType.CREATE_USER);
        }

        @Override
        protected Class<TestDto> getPayloadClass() {
            return TestDto.class;
        }

        @Override
        protected void handle(TestDto payload) {
            this.lastHandled = payload;
        }
    }
}