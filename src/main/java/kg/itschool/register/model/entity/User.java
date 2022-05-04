package kg.itschool.register.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_users", schema = "register_details")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Formula("SELECT p.password " +
            "FROM tb_passwords p " +
            "WHERE is_current_password = TRUE " +
            "LIMIT 1")
    String password;

    @ToString.Exclude
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    List<Password> passwords;

    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id")
    Role role;

    @Column(name = "is_account_non_expired")
    Boolean isAccountNonExpired;

    @Column(name = "is_account_non_locked")
    Boolean isAccountNonLocked;

    @Column(name = "is_credentials_non_expired")
    Boolean isCredentialsNonExpired;

    @Column(name = "is_enabled")
    Boolean isEnabled;

    @Column(name = "last_activity")
    LocalDateTime lastActivity;

    @Column(name = "locked_until")
    LocalDateTime lockedUntil;

    @Column(name = "login_attempts", columnDefinition = "INT DEFAULT 0")
    Integer loginAttempts;

    // AUTHORIZATION
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role
                .getAuthorities()
                .stream()
                .filter(Authority::getIsEnabled)
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
    }

    // AUTHENTICATION
    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}