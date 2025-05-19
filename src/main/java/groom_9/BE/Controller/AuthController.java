package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.Dto;
import groom_9.BE.DTO.MemberRequestDto;
import groom_9.BE.DTO.UserDto;
import groom_9.BE.DTO.UserResponseDto;
import groom_9.BE.Domain.User;
import groom_9.BE.Service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return ApiResponse.onFailure(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "카카오 로그인 이동 URL 생성",
            description = "사용자가 카카오 로그인 버튼 클릭 시 이동할 인증 URL을 반환합니다."
    )
    @GetMapping("/request")
    public ResponseEntity<String> getKaKaoUri() {
        String requestUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";
        return ResponseEntity.ok(requestUrl);
    }

    // 전달 받은 인증 코드에 접근해 토큰 요청 후 user 확인
    @Hidden
    @GetMapping("/member/kakao")
    public ResponseEntity<ApiResponse<Object>> getKakaoUser(@RequestParam(value = "code") String code) {
        log.info("인가 코드를 이용하여 토큰을 받습니다.");
        String accessToken = authService.getAccessToken(code);
        log.info("토큰에 대한 정보입니다.{}",accessToken);
        Map<String, Object> userInfo = authService.getUserInfo(accessToken);
        log.info("회원 정보 입니다.{}",userInfo);

        String kakaoId = userInfo.get("id").toString();
        String nickName = userInfo.get("nickname").toString();
        String imageUrl = userInfo.get("imageUrl").toString();

        UserResponseDto userResponse = authService.saveUserKakao(kakaoId, nickName, imageUrl);

        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, userResponse);
    }

    @Operation(summary = "회원 정보 입력 및 가입 완료",
            description = "회원 가입 시 나이, 성별 등의 정보를 입력합니다."
    )
    @PostMapping("/member/info")
    public ResponseEntity<ApiResponse<Object>> setMemberInfo(@RequestBody MemberRequestDto memberRequest){
        log.info("입력받은 memberRequest={}",memberRequest);
        User user = authService.setMemberInfo(memberRequest);
        String id = user.getId().toHexString();

        return ApiResponse.onSuccess("성공적으로 회원가입 되었습니다. id=", HttpStatus.OK, id);
    }

    @Operation(summary = "유저 프로필 정보 조회",
            description = "사용자 ID로 유저의 프로필 정보(닉네임, 이미지, 키워드 등)를 조회합니다."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUser(@PathVariable("userId") String id) {
        log.info("입력받은 Id={}",id);

        UserDto userDto = new UserDto(authService.findUserInfo(id));
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, userDto);
    }

    @Operation(summary = "회원 탈퇴",
            description = "사용자 ID로 회원 탈퇴를 진행합니다."
    )
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable("userId") String id) {
        log.info("입력받은 Id={}",id);
        authService.deleteUser(id);
        return ApiResponse.onSuccess("성공적으로 삭제되었습니다.", HttpStatus.OK);
    }

}
