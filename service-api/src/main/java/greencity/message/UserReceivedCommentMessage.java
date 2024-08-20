package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
public class UserReceivedCommentMessage extends CommentMessage implements Serializable {
    @NotEmpty(message = "Author name cannot be empty")
    private String authorName;

    @Builder
    public UserReceivedCommentMessage(String receiverName, String receiverEmail, String language,
        String elementName, String commentText, LocalDateTime creationDate,
        Long commentedElementId, String baseLink, String authorName) {
        super(receiverName, receiverEmail, language, elementName, commentText, creationDate, commentedElementId,
            baseLink);
        this.authorName = authorName;
    }
}