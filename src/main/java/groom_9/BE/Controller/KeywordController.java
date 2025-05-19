package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.RoutineDto;
import groom_9.BE.Service.AuthService;
import groom_9.BE.Service.KeywordService;
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


    // 유저가 keyword 선택 앤 저장
    @PostMapping("/{userId}/keyword")
    public ResponseEntity<ApiResponse<String>> setKeyword(@PathVariable("userId") String userId, @RequestParam("keyword") String keyword) {
        keywordService.setKeyword(userId, keyword);

        return ApiResponse.onSuccess("성공입니다. userId=", HttpStatus.OK, userId);
    }


    // keyword에 따른 루틴 추천
    @PostMapping("/{userId}/routines")
    public ResponseEntity<ApiResponse<String>> setRoutine(@PathVariable("userId") String userId, @RequestBody List<RoutineDto> routineDtoList) {
        keywordService.setRoutine(userId, routineDtoList);

        return ApiResponse.onSuccess("성공입니다. userId=", HttpStatus.OK, userId);
    }

}
