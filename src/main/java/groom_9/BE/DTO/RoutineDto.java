package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Getter
public class RoutineDto {
    @Schema(description = "루틴 내용", example = "하루 1바퀴 걷기")
    private String content;

    @Schema(description = "루틴과 연관된 이모지", example = "🚶‍♂️")
    private String emoji;
}
