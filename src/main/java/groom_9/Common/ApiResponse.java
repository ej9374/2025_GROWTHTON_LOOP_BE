package groom_9.Common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가
@JsonPropertyOrder({"timestamp", "code", "message", "result"}) // JSON 응답 시 순서를 정의
public class ApiResponse<T> {

    @JsonProperty("timestamp")
    private final String timestamp = String.valueOf(System.currentTimeMillis());

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;


    // 성공한 경우 응답 생성
    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiResponse<>(String.valueOf(httpStatus.value()), message), httpStatus);
    }

    // 성공한 경우 응답 생성 (결과 데이터 포함)
    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiResponse<>(String.valueOf(httpStatus.value()), HttpStatus.OK.getReasonPhrase()), httpStatus);
    }

    // 실패한 경우 응답 생성, 상태 코드 포함 가능
    public static <T> ResponseEntity<ApiResponse<T>> onFailure(String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiResponse<>(String.valueOf(httpStatus.value()), message), httpStatus);
    }
}