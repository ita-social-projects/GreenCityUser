package greencity.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "employee_authorities")
@EqualsAndHashCode
@Entity
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "description_uk")
    private String descriptionUk;

    @ManyToMany(mappedBy = "authorities",
        cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<User> employees;

    @ManyToMany
    @JoinTable(
        name = "positions_authorities_mapping",
        joinColumns = @JoinColumn(name = "authorities_id"),
        inverseJoinColumns = @JoinColumn(name = "position_id"))
    private List<Position> positions;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private AuthorityCategory category;
}
