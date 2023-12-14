package greencity.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.exception.exceptions.NotValidBooleanValueException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BooleanValueDeserializerTest {

    private final BooleanValueDeserializer deserializer = new BooleanValueDeserializer();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializeInvalidBooleanTest() throws IOException {
        JsonParser jsonParser = objectMapper.getFactory().createParser("invalid value");
        assertThrows(NotValidBooleanValueException.class, () -> deserializer.deserialize(jsonParser, null));
    }

    @Test
    void deserializeMissingValueTest() throws IOException {
        JsonParser jsonParser = objectMapper.getFactory().createParser("");
        assertThrows(NotValidBooleanValueException.class, () -> deserializer.deserialize(jsonParser, null));
    }
}
