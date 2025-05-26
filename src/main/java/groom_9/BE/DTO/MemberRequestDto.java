package groom_9.BE.DTO;

import groom_9.BE.Domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class MemberRequestDto {
    @Schema(description = "사용자 ID (ObjectId의 16진수 문자열)", example = "60c72b2f9f1b2c001c8e4b9a")
    private String userId;

    @Schema(description = "사용자 네임", example = "사용자")
    private String name;

    @Schema(description = "사용자 나이", example = "30")
    private Integer age;

    @Schema(description = "성별 (MALE, FEMALE 등)", example = "MALE")
    private Gender gender;
}
