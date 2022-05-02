package kg.itschool.register.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthorityDto implements Serializable {
    private final String authorityName;
    private final Boolean isEnabled;
}
