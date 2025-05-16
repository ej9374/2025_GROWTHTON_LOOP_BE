package groom_9.BE.Controller;

import groom_9.BE.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> getKakaoUser(@RequestParam(value = "code") String code) {
        log.info("인가 코드를 이용하여 토큰을 받습니다.");
        String accessToken = authService.getAccessToken(code);
        log.info("토큰에 대한 정보입니다.{}",accessToken);
        Map<String, Object> userInfo = authService.getUserInfo(accessToken);
        log.info("회원 정보 입니다.{}",userInfo);

        String kakaoUserId = userInfo.get("kakaoUserId").toString();
        String nickName = userInfo.get("nickName").toString();

        authService.saveUser(kakaoUserId, nickName);


        return ResponseEntity.ok("성공적으로 로그인 되었습니다.");
    }
}
