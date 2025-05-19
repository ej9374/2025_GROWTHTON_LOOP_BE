package groom_9.BE.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KeywordAndRoutineResponseDto {
    private String keywordContent;
    private List<String> routines;

    public KeywordAndRoutineResponseDto(String keywordContent, List<String> routineContents) {
        this.keywordContent = keywordContent;
        this.routines = routineContents;
    }
}
