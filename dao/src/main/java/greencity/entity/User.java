package greencity.entity;

import greencity.dto.user.RegistrationStatisticsDtoResponse;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;

@Entity
@SqlResultSetMapping(
    name = "monthsStatisticsMapping",
    classes = {
        @ConstructorResult(
            targetClass = RegistrationStatisticsDtoResponse.class,
            columns = {
                @ColumnResult(name = "month", type = Integer.class),
                @ColumnResult(name = "count", type = Long.class)
            })
    })
@NamedNativeQuery(name = "User.findAllRegistrationMonths",
    query = """
        SELECT EXTRACT(MONTH FROM date_of_registration) - 1 as month, count(date_of_registration) FROM users/
        WHERE EXTRACT(YEAR from date_of_registration) = EXTRACT(YEAR FROM CURRENT_DATE)/
        GROUP BY month/
        """,
    resultSetMapping = "monthsStatisticsMapping")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
@EqualsAndHashCode(exclude = {"verifyEmail", "ownSecurity", "refreshTokenKey", "restorePasswordEmail", "userFriends"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(value = EnumType.ORDINAL)
    @JdbcType(IntegerJdbcType.class)
    private UserStatus userStatus;

    @Column(nullable = false)
    private LocalDateTime dateOfRegistration;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST)
    private OwnSecurity ownSecurity;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private VerifyEmail verifyEmail;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST)
    private RestorePasswordEmail restorePasswordEmail;

    @Enumerated(value = EnumType.ORDINAL)
    @JdbcType(IntegerJdbcType.class)
    private EmailNotification emailNotification;

    @Column(name = "refresh_token_key", nullable = false)
    private String refreshTokenKey;

    @Column(name = "profile_picture")
    private String profilePicturePath;

    @Builder.Default
    @OneToMany
    @JoinTable(name = "users_friends",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
    private List<User> userFriends = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserAchievement> userAchievements = new ArrayList<>();

    @Column(name = "rating")
    private Double rating;

    @Column(name = "first_name")
    private String firstName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_location")
    private UserLocation userLocation;

    @Column(name = "user_credo")
    private String userCredo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @Column(name = "social_networks")
    private List<SocialNetwork> socialNetworks;

    @Column(name = "show_location")
    private Boolean showLocation;

    @Column(name = "show_eco_place")
    private Boolean showEcoPlace;

    @Column(name = "show_shopping_list")
    private Boolean showShoppingList;

    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAction> userActions = new ArrayList<>();

    @Column(columnDefinition = "varchar(60)")
    private String uuid;

    @ManyToOne
    private Language language;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserDeactivationReason> userDeactivationReasons;

    @ManyToMany
    @JoinTable(
        name = "employee_authorities_mapping",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<Authority> authorities;

    @ManyToMany
    @JoinTable(
        name = "employee_positions_mapping",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "position_id"))
    private List<Position> positions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserNotificationPreference> notificationPreferences = new HashSet<>();
}