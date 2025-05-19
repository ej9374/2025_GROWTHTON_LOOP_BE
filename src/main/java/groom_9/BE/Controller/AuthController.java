package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.Dto;
import groom_9.BE.DTO.MemberRequestDto;
import groom_9.BE.DTO.UserDto;
import groom_9.BE.DTO.UserResponseDto;
import groom_9.BE.Domain.User;
import groom_9.BE.Service.AuthService;
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

    // 카카오 접근을 위한 인증코드 요청 과정 << 프론트엔드에서 이 창을 띄워주면 됨!
    @GetMapping("/request")
    public ResponseEntity<String> getKaKaoUri() {
        String requestUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";
        return ResponseEntity.ok(requestUrl);
    }

    // 전달 받은 인증 코드에 접근해 토큰 요청 후 user 확인
    @Description("회원이 소셜 로그인을 마치면 자동으로 실행되는 API입니다. 인가 코드를 이용해 토큰을 받고, 해당 토큰으로 사용자 정보를 조회합니다." +
            "사용자 정보를 이용하여 서비스에 회원가입합니다.")
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

    @PostMapping("/member/info")
    public ResponseEntity<ApiResponse<Object>> setMemberInfo(@RequestBody MemberRequestDto memberRequest){
        log.info("입력받은 memberRequest={}",memberRequest);
        User user = authService.setMemberInfo(memberRequest);
        String id = user.getId().toHexString();

        return ApiResponse.onSuccess("성공적으로 회원가입 되었습니다. id=", HttpStatus.OK, id);
    }



    // 로그인 및 마이페이지 프로필 이미지/ 닉네임/ 목표 키워드 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUser(@PathVariable("userId") String id) {
        log.info("입력받은 Id={}",id);

        UserDto userDto = new UserDto(authService.findUserInfo(id));
        return ApiResponse.onSuccess("성공입니다.", HttpStatus.OK, userDto);
    }


    // 회원탈퇴
    @GetMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable("userId") String id) {
        log.info("입력받은 Id={}",id);
        authService.deleteUser(id);
        return ApiResponse.onSuccess("성공적으로 삭제되었습니다.", HttpStatus.OK);
    }

}
