package kg.itschool.register.model.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {

    @Email
    String username;

    @Length(min = 2)
    String password;

    @Length(min = 2)
    String roleName;

    @NotBlank
    @Length(min = 2)
    String firstName;

    @NotBlank
    @Length(min = 2)
    String lastName;

    String patronymic;
}
