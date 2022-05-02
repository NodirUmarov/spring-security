package kg.itschool.register.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RoleDto implements Serializable {
    private final String id;
    private final String roleName;
    private final List<AuthorityDto> authorities;

}
