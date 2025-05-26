package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRoutine {
    @Schema(description = "삭제할 루틴 이름", example = "아침 스트레칭")
    private String routine;;
}
