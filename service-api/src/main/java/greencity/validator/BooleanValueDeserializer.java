package greencity.validator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import static greencity.constant.ErrorMessage.NOT_VALID_BOOLEAN_VALUE;
import greencity.exception.exceptions.validation.NotValidBooleanValueException;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class BooleanValueDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        try {
            return jsonParser.getBooleanValue();
        } catch (JsonParseException exception) {
            throw new NotValidBooleanValueException(NOT_VALID_BOOLEAN_VALUE.formatted(jsonParser.getText(),
                jsonParser.getCurrentName()));
        }
    }
}
