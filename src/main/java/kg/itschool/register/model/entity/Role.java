package kg.itschool.register.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_roles", schema = "register_details")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AbstractAuditable<User, Long> {

    @Column(name = "role_name", unique = true, nullable = false)
    String roleName;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_has_authority",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_name", referencedColumnName = "authority_name"),
            schema = "register_details"
    )
    List<Authority> authorities;

}
