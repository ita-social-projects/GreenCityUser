package greencity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @ManyToMany(mappedBy = "authorities", cascade = CascadeType.ALL)
    private List<User> employees;

    @ManyToMany
    @JoinTable(
        name = "positions_authorities_mapping",
        joinColumns = @JoinColumn(name = "authorities_id"),
        inverseJoinColumns = @JoinColumn(name = "position_id"))
    private List<Position> positions;
}
