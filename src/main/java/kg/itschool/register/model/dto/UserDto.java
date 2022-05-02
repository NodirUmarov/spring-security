package kg.itschool.register.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    private final String username;
    private final RoleDto role;

    @JsonIgnore
    private final String password;

}
