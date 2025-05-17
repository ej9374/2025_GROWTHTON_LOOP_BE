package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.Dto;
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

    @GetMapping("/fail")
    public ResponseEntity<ApiResponse<Object>> fail() {
        return ApiResponse.onFailure("실패입니다.", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<Object>> success() {
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK);
    }

    @GetMapping("/success2")
    public ResponseEntity<ApiResponse<Dto>> success2() {
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, new Dto("gildong", 3));
    }
}
