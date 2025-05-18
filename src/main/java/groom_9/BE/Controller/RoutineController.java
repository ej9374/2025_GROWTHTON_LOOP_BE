package groom_9.BE.Controller;


import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.MonthlyReportDto;
import groom_9.BE.DTO.QuestionDto;
import groom_9.BE.DTO.RecordDto;
import groom_9.BE.DTO.SuccessRecordDto;
import groom_9.BE.Service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RoutineController {
    private final RoutineService routineService;

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<String>> home(@RequestHeader ObjectId userId) {
        String keyword = routineService.home(userId);
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, keyword);
    }

    @PostMapping("/home")
    public ResponseEntity<ApiResponse<String>> record(@RequestHeader ObjectId userId, @RequestBody RecordDto recordDto) {
        if(routineService.successRecord(recordDto.getRecords())) {
            return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
        }
        else {
            return ApiResponse.onFailure("오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/calendar/{month}")
    public ResponseEntity<ApiResponse<List<SuccessRecordDto>>> getCalendar(
            @RequestHeader ObjectId userId, // 사용자 ID를 통해 해당 사용자의 기록만 조회하도록 구현할 수 있습니다.
            @PathVariable int month) {
        List<SuccessRecordDto> calendarData = routineService.calendar(month, userId);
        return ApiResponse.onSuccess(month + "월 실천 내역", HttpStatus.OK, calendarData);
    }

    @GetMapping("/report/{month}")
    public ResponseEntity<ApiResponse<MonthlyReportDto>> getMonthlyReport(
            @RequestHeader ObjectId userId,
            @PathVariable int month,
            @RequestBody QuestionDto questionDto
            ) {
        MonthlyReportDto reportData = routineService.getMonthlyReport(month, userId, questionDto.getReflectionQuestion(), questionDto.getAnswer());
        return ApiResponse.onSuccess(month + "월 리포트", HttpStatus.OK, reportData);
    }

}
