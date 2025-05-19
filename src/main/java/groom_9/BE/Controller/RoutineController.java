package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.*;
import groom_9.BE.Service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RoutineController {
    private final RoutineService routineService;

    @GetMapping("/user")
    public HttpEntity<ApiResponse<Optional<UserInfoResponseDto>>> getUser(@RequestParam ObjectId userId) {
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, routineService.getUserInfo(userId));
    }


    /**
     * {
     * "timestamp": "2025-05-19T17:19:11.781096",
     * "code": "200",
     * "message": "OK",
     * "result": {
     * "keywordContent": "데이터베이스",
     * "routines": [
     * "새로운 루틴 1",
     * "또 다른 새로운 루틴",
     * "잊지 말아야 할 루틴"
     * ]
     * }
     * }
     */
    @GetMapping("/home")
    public ResponseEntity<ApiResponse<KeywordAndRoutineResponseDto>> home(@RequestParam ObjectId userId) {
        log.info("userId={}", userId);
        log.info("접속성공");
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, routineService.getKeywordAndRoutines(userId));
    }

    @PostMapping("home") // 루틴 성공으로 바꾸기
    public ResponseEntity<ApiResponse<String>> homePost(@RequestParam ObjectId userId, @RequestBody SuccessRoutinesDto successRoutinesDto) {
        log.info("userId={}", userId);
        if (routineService.successRoutine(userId, successRoutinesDto)) {
            return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
        }
        else {
            return ApiResponse.onFailure("저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     {
     "timestamp": "1747642471299",
     "code": "200",
     "message": "OK",
     "result": {
     "매일 6시 기상": 3.225806451612903,
     "명상 10분": 3.225806451612903
     }
     }
     */
    @GetMapping("/report") //리포트 보기 가능
    public ResponseEntity<ApiResponse<Map<String, Double>>> report(@RequestParam ObjectId userId) {
        Map<String, Double> stringDoubleMap = routineService.calculateIndividualRoutineSuccessRate(userId);
        return  ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, stringDoubleMap);
    }


    @PostMapping("/retrospect") //회고 페이지
    public ResponseEntity<ApiResponse<String>> retrospect (@RequestParam ObjectId userId, @RequestBody FeedbackAnswerDto feedbackAnswerDto) {
            routineService.saveFeedbacks(userId, feedbackAnswerDto.getAnswer());
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }


    @PostMapping("/routines") // 루틴 추가
    public ResponseEntity<ApiResponse<String>> addRoutines (@RequestParam ObjectId userId, @RequestBody NewRoutineListDto newRoutineListDto) {
        routineService.addRoutine(userId, newRoutineListDto.getRoutineList());
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }

    @PatchMapping("/routines") // 루틴 수정
    public ResponseEntity<ApiResponse<String>> updateRoutines (@RequestParam ObjectId userId, @RequestBody UpdateRoutine updateRoutine) {
        routineService.updateAllRoutinesByContent(userId, updateRoutine.getOldRoutine(), updateRoutine.getNewRoutine());
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }

    @DeleteMapping("/routines")
    public ResponseEntity<ApiResponse<String>> deleteRoutines (@RequestParam ObjectId userId, @RequestBody DeleteRoutine deleteRoutine) {
        if(routineService.deleteRoutine(userId, deleteRoutine)) {
            return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
        }
        else {
            return ApiResponse.onFailure("삭제에 실패했습니다. 해당 루틴이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }


}
