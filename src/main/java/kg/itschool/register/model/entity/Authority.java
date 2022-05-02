package kg.itschool.register.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_authorities", schema = "register_details")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Authority {

    @Id
    @Column(name = "authority_name", nullable = false, unique = true)
    String authorityName;

    @Column(name = "is_enabled", nullable = false)
    Boolean isEnabled;
}
