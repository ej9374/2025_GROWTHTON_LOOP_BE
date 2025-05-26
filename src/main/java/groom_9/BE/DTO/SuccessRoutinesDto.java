package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class SuccessRoutinesDto {

    @Schema(
            description = "성공한 루틴 이름 목록",
            example = "[\"하루 1바퀴 걷기\", \"물 2L 마시기\"]"
    )
    List<String> routines;
}
