package groom_9.BE.Common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가
@JsonPropertyOrder({"timestamp", "code", "message", "result"}) // JSON 응답 시 순서를 정의
public class ApiResponse<T> {

    @JsonProperty("timestamp")
    private final String timestamp = String.valueOf(LocalDateTime.now());

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("result")
    private T result;

    // 성공한 경우 응답 생성 (결과 데이터 없음)
    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiResponse<>("OK:200", message, null), httpStatus);

    }

    // 성공한 경우 응답 생성 (결과 데이터 포함)
    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(String message, HttpStatus httpStatus, T result) {
        return new ResponseEntity<>(new ApiResponse<>(String.valueOf(httpStatus.value()), HttpStatus.OK.getReasonPhrase(), result), httpStatus);
    }

    // 실패한 경우 응답 생성 (결과 데이터 없음)
    public static <T> ResponseEntity<ApiResponse<T>> onFailure(String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiResponse<>(String.valueOf(httpStatus.value()), message, null), httpStatus);
    }
}