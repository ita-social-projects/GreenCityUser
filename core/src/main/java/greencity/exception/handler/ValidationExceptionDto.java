package greencity.exception.handler;

import lombok.Data;
import org.springframework.validation.FieldError;

import java.io.Serializable;

/**
 * Dto for sending information about bad fields while validation.
 *
 * @author Nazar Stasyuk
 */
@Data
public class ValidationExceptionDto implements Serializable {
    private String name;
    private String message;

    /**
     * Generated javadoc, must be replaced with real one.
     */
    public ValidationExceptionDto(FieldError error) {
        this.name = error.getField();
        this.message = error.getDefaultMessage();
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    public ValidationExceptionDto(String name, String message) {
        this.name = name;
        this.message = message;
    }
}
