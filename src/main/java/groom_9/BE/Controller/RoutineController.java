package groom_9.BE.Controller;


import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.RecordDto;
import groom_9.BE.Service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
