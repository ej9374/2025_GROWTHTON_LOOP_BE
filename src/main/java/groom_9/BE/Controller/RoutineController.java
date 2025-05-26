package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.*;
import groom_9.BE.Service.RoutineService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "유저 정보 조회",
            description = "ObjectId로 사용자의 기본 정보(이름, 키워드 등)를 조회합니다."
    )
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
    @Operation(
            summary = "홈 화면 루틴 조회",
            description = "현재 사용자의 키워드와 해당 키워드에 따른 루틴 리스트를 반환합니다."
    )
    @GetMapping("/home")
    public ResponseEntity<ApiResponse<KeywordAndRoutineResponseDto>> home(@RequestParam ObjectId userId) {
        log.info("userId={}", userId);
        log.info("접속성공");
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, routineService.getKeywordAndRoutines(userId));
    }

    @Operation(
            summary = "루틴 성공 처리",
            description = "사용자가 수행한 루틴 목록을 전달받아 상태를 성공으로 업데이트합니다."
    )
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
    @Operation(
            summary = "루틴별 성공률 리포트 조회",
            description = "각 루틴에 대한 사용자의 성공률을 계산해 리포트 형식으로 반환합니다."
    )
    @GetMapping("/report") //리포트 보기 가능
    public ResponseEntity<ApiResponse<Map<String, Double>>> report(@RequestParam ObjectId userId) {
        Map<String, Double> stringDoubleMap = routineService.calculateIndividualRoutineSuccessRate(userId);
        return  ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, stringDoubleMap);
    }

    @Operation(
            summary = "회고 피드백 저장",
            description = "사용자가 입력한 회고 답변을 MongoDB에 저장합니다."
    )
    @PostMapping("/retrospect") //회고 페이지
    public ResponseEntity<ApiResponse<String>> retrospect (@RequestParam ObjectId userId, @RequestBody FeedbackAnswerDto feedbackAnswerDto) {
            routineService.saveFeedbacks(userId, feedbackAnswerDto.getAnswer());
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }

    @Operation(
            summary = "루틴 추가",
            description = "새로운 루틴 리스트를 받아 사용자에게 추가합니다."
    )
    @PostMapping("/routines") // 루틴 추가
    public ResponseEntity<ApiResponse<String>> addRoutines (@RequestParam ObjectId userId, @RequestBody NewRoutineListDto newRoutineListDto) {
        routineService.addRoutine(userId, newRoutineListDto.getRoutineList());
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }

    @Operation(
            summary = "루틴 수정",
            description = "기존 루틴 이름을 기준으로 새로운 이름으로 업데이트합니다."
    )
    @PatchMapping("/routines") // 루틴 수정
    public ResponseEntity<ApiResponse<String>> updateRoutines (@RequestParam ObjectId userId, @RequestBody UpdateRoutine updateRoutine) {
        routineService.updateAllRoutinesByContent(userId, updateRoutine.getOldRoutine(), updateRoutine.getNewRoutine());
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }

    @Operation(
            summary = "루틴 삭제",
            description = "사용자의 루틴 중 하나를 삭제합니다. 존재하지 않으면 실패 응답을 반환합니다."
    )
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
