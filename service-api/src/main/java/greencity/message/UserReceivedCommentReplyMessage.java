package greencity.message;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReceivedCommentReplyMessage implements Serializable {
    @NotEmpty(message = "Author name cannot be empty")
    private String authorName;

    @NotEmpty(message = "Receiver name cannot be empty")
    private String receiverName;

    @NotEmpty(message = "Receiver email cannot be empty")
    private String receiverEmail;

    @NotEmpty(message = "Language cannot be empty")
    private String language;

    @NotEmpty(message = "Name of element cannot be null")
    private String elementName;

    @NotEmpty(message = "Comment cannot be empty")
    private String commentText;

    @NotNull(message = "Date cannot be null")
    private LocalDateTime creationDate;

    @NotNull(message = "Element ID cannot be null")
    private Long commentedElementId;

    @NotEmpty(message = "Parent comment cannot be empty")
    private String parentCommentText;

    @NotEmpty(message = "Parent comment author name cannot be empty")
    private String parentCommentAuthorName;

    @NotNull(message = "Parent comment creation date cannot be null")
    private LocalDateTime parentCommentCreationDate;

    @NotEmpty(message = "Base link cannot be null")
    private String baseLink;
}

