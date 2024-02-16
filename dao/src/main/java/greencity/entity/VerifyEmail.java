package greencity.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@Table(name = "verify_emails")
public class VerifyEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.DETACH)
    private User user;

    private String token;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
}
