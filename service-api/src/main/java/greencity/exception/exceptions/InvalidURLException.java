package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;
import lombok.experimental.StandardException;

@StandardException
public class InvalidURLException extends ConstraintDeclarationException {
}
