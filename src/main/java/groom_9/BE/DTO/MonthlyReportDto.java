package groom_9.BE.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyReportDto {
    private int totalRoutines;
    private double averageSuccessRate;
    private int totalSuccessCount;
    private String reflectionQuestion;
    private String answer;
}
