package kg.itschool.register.repository;

import kg.itschool.register.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Modifying
    @Query(value = "UPDATE register_details.tb_users SET last_activity = NOW() WHERE username = ?1",
            nativeQuery = true)
    void updateLastActivity(String username);

}