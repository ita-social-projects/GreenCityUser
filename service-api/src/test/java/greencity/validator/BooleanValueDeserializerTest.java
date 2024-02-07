package greencity.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.exception.exceptions.NotValidBooleanValueException;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BooleanValueDeserializerTest {

    private final BooleanValueDeserializer deserializer = new BooleanValueDeserializer();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @ValueSource(strings = {"invalid value", ""})
    void deserializeInvalidValueTest(String jsonValue) throws IOException {
        JsonParser jsonParser = objectMapper.getFactory().createParser(jsonValue);
        assertThrows(NotValidBooleanValueException.class, () -> deserializer.deserialize(jsonParser, null));
    }
}
