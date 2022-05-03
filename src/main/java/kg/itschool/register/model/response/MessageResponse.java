package kg.itschool.register.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageResponse implements Serializable {

    String message;

    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }
}
