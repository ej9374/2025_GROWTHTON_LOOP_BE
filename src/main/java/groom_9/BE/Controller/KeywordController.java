package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.RoutineDto;
import groom_9.BE.Service.AuthService;
import groom_9.BE.Service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;
    private final AuthService authService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return ApiResponse.onFailure(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Operation(
            summary = "사용자 키워드 선택 및 저장",
            description = "사용자가 선택한 키워드를 저장합니다. userId와 키워드 문자를 전달해야 합니다."
    )
    @PostMapping("/{userId}/keyword")
    public ResponseEntity<ApiResponse<String>> setKeyword(@PathVariable("userId") String userId, @RequestParam("keyword") String keyword) {
        keywordService.setKeyword(userId, keyword);
        return ApiResponse.onSuccess("성공입니다. userId=", HttpStatus.OK, userId);
    }

    @Operation(
            summary = "키워드에 따른 루틴 추천 및 저장",
            description = "키워드에 따른 루틴 리스트를 받아 사용자에 저장합니다."
    )
    @PostMapping("/{userId}/routines")
    public ResponseEntity<ApiResponse<String>> setRoutine(@PathVariable("userId") String userId, @RequestBody List<RoutineDto> routineDtoList) {
        keywordService.setRoutine(userId, routineDtoList);
        return ApiResponse.onSuccess("성공입니다. userId=", HttpStatus.OK, userId);
    }

}
