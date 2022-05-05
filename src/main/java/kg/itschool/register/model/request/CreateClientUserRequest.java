package kg.itschool.register.model.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateClientUserRequest {

    String firstName;
    String lastName;
    String patronymic;
    String email;

}
