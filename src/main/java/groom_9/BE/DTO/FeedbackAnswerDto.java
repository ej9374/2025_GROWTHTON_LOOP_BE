package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class FeedbackAnswerDto {
    @Schema(
            description = "사용자가 입력한 회고 답변 리스트",
            example = "[\"오늘 루틴을 모두 완료해서 뿌듯했어요\", \"물 마시는 루틴은 아직 어렵네요\"]"
    )
    List<String> answer;
}
