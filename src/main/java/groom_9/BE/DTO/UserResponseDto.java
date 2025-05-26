package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {
    @Schema(description = "사용자 ID (ObjectId의 16진수 문자열)", example = "60c72b2f9f1b2c001c8e4b9a")
    private String userId;

    @Schema(description = "신규 사용자 여부 (true: 회원가입 완료 후 로그인 창으로 이동, false: 회원가입 절차 계속 진행)", example = "true")
    private boolean isNewUser;
}
