package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRoutine(@PathVariable ObjectId id) {
        if (true) {
            return ApiResponse.onSuccess("루틴이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } else {
            return ApiResponse.onFailure("루틴을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null);
        }
    }


    @PostMapping("/hello")
    public ResponseEntity<ApiResponse<Objects>> createRoutine() {
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }

    @GetMapping("/end")
    public ResponseEntity<ApiResponse<Objects>> getAllRoutines() {
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }
}
