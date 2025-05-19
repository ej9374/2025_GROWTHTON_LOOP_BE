package groom_9.BE.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SuccessRecordDto {
    private LocalDate date;
    private List<String> routineContents; // 성공한 루틴의 내용
}
