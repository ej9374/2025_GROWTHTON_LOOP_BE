package groom_9.BE.DTO;

import groom_9.BE.Domain.Gender;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class MemberRequestDto {
    String userId;
    Integer age;
    Gender gender;
}
