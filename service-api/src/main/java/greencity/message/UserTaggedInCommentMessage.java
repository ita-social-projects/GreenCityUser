package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
public class UserTaggedInCommentMessage extends CommentMessage implements Serializable {
    @NotEmpty(message = "Tagger name cannot be empty")
    private String taggerName;

    @Builder
    public UserTaggedInCommentMessage(String receiverName, String receiverEmail, String language,
        String elementName, String commentText, LocalDateTime creationDate,
        Long commentedElementId, String baseLink, String taggerName) {
        super(receiverName, receiverEmail, language, elementName, commentText, creationDate, commentedElementId,
            baseLink);
        this.taggerName = taggerName;
    }
}