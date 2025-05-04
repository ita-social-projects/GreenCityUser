package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "languages")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(
    exclude = {"adviceTranslations", "toDoListItemTranslations", "habitTranslations", "factOfTheDayTranslations"})
@ToString(
    exclude = {"adviceTranslations", "toDoListItemTranslations", "habitTranslations", "factOfTheDayTranslations"})
@Builder
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 35)
    private String code;

    @OneToMany(mappedBy = "language")
    private List<User> users;
}
