package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KeywordAndRoutineResponseDto {
    @Schema(description = "사용자가 선택한 키워드", example = "운동")
    private String keywordContent;

    @Schema(description = "해당 키워드에 기반한 추천 루틴 목록", example = "[\"하루 1바퀴 걷기\", \"스트레칭 10분\"]")
    private List<String> routines;

    public KeywordAndRoutineResponseDto(String keywordContent, List<String> routineContents) {
        this.keywordContent = keywordContent;
        this.routines = routineContents;
    }
}
