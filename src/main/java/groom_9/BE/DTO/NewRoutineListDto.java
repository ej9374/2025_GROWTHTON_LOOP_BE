package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewRoutineListDto {
    @Schema(
            description = "추가할 루틴들의 이름 리스트",
            example = "[\"아침 스트레칭\", \"명상 10분\"]"
    )
    List<String> routineList;

}
