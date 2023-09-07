package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class UserRatingDto {
    private Long id;

    private String email;

    private Double rating;

    /**
     * Constructs a new UserRatingDto object with the specified values.
     *
     * @param id     The unique identifier of the user rating.
     * @param email  The email address associated with the user rating.
     * @param rating The rating value.
     *
     * @JsonCreator and @JsonProperty annotations are used to indicate this
     *              constructor should be used for JSON deserialization, and to map
     *              the JSON properties to constructor parameters respectively.
     */
    @JsonCreator
    public UserRatingDto(
        @JsonProperty("id") Long id,
        @JsonProperty("email") String email,
        @JsonProperty("rating") Double rating) {
        this.id = id;
        this.email = email;
        this.rating = rating;
    }
}
