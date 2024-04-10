package greencity.message;

import java.io.Serializable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public final class SendChangePlaceStatusEmailMessage implements Serializable {
    @NotEmpty(message = "Author's first name cannot be empty")
    private String authorFirstName;

    @NotEmpty(message = "Place name cannot be empty")
    private String placeName;

    @Pattern(regexp = "^(approved|declined)$", message = "Place status must be 'approved' or 'declined'")
    private String placeStatus;

    @NotEmpty(message = "Author's email cannot be empty")
    private String authorEmail;
}
