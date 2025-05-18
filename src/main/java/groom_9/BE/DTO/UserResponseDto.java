package groom_9.BE.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {
    private String userId;
    private boolean isNewUser;
}
