package greencity.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(
    exclude = {"userAchievements"})
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 300)
    private String name;
    @Column(nullable = false, length = 300)
    private String nameEng;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "achievement", fetch = FetchType.LAZY)
    private List<UserAchievement> userAchievements;

    @ManyToOne
    private AchievementCategory achievementCategory;

    @Column(nullable = false)
    private Integer condition;
}