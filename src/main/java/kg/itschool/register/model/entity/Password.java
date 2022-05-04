package kg.itschool.register.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_passwords", schema = "register_details")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Password extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    @Column(name = "password", nullable = false, updatable = false)
    String password;

    @Column(name = "is_current_password", nullable = false)
    Boolean isCurrentPassword;

    @Column(name = "password_expiration", nullable = false, updatable = false)
    LocalDateTime passwordExpiration;

    @PrePersist
    private void onCreate() {
        passwordExpiration = LocalDateTime.now().plusMonths(2);
    }
}
