package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
public class UserReceivedCommentReplyMessage extends CommentMessage implements Serializable {
    @NotEmpty(message = "Author name cannot be empty")
    private String authorName;

    @NotEmpty(message = "Parent comment cannot be empty")
    private String parentCommentText;

    @NotEmpty(message = "Parent comment author name cannot be empty")
    private String parentCommentAuthorName;

    @NotNull(message = "Parent comment creation date cannot be null")
    private LocalDateTime parentCommentCreationDate;

    @Builder
    public UserReceivedCommentReplyMessage(String receiverName, String receiverEmail, String language,
        String elementName, String commentText, LocalDateTime creationDate,
        Long commentedElementId, String baseLink, String authorName, String parentCommentText,
        String parentCommentAuthorName, LocalDateTime parentCommentCreationDate) {
        super(receiverName, receiverEmail, language, elementName, commentText, creationDate, commentedElementId,
            baseLink);
        this.authorName = authorName;
        this.parentCommentText = parentCommentText;
        this.parentCommentAuthorName = parentCommentAuthorName;
        this.parentCommentCreationDate = parentCommentCreationDate;
    }
}