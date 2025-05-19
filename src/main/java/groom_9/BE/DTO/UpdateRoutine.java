package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoutine {

    @Schema(description = "기존 루틴 이름", example = "하루 1바퀴 걷기")
    private String OldRoutine;

    @Schema(description = "새로 바꿀 루틴 이름", example = "저녁 2바퀴 걷기")
    private String NewRoutine;
}
