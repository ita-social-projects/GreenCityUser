package greencity.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity

@Table(name = "reasons_for_user_deactivation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeactivationReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "date_of_deactivation")
    private LocalDateTime dateTimeOfDeactivation;
    @Column(name = "reason")
    private String reason;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_user")
    private User user;
}
