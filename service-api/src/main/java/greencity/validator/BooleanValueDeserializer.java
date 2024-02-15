package greencity.validator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import greencity.exception.exceptions.NotValidBooleanValueException;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static greencity.constant.ErrorMessage.NOT_VALID_BOOLEAN_VALUE;

@Component
public class BooleanValueDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        try {
            return jsonParser.getBooleanValue();
        } catch (JsonParseException exception) {
            throw new NotValidBooleanValueException(String.format(NOT_VALID_BOOLEAN_VALUE, jsonParser.getText(),
                jsonParser.getCurrentName()));
        }
    }
}
