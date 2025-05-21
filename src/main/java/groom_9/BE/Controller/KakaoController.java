package groom_9.BE.Controller;

import groom_9.BE.Common.ApiResponse;
import groom_9.BE.DTO.UserResponseDto;
import groom_9.BE.Service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/api")
@Slf4j
public class KakaoController {

    private final AuthService authService;

    @Value("${frontend.redirect_uri}")
    private String redirectUri;

    public KakaoController(AuthService authService) {
        this.authService = authService;
    }

    // 전달 받은 인증 코드에 접근해 토큰 요청 후 user 확인
    @Hidden
    @GetMapping("/member/kakao")
    public String getKakaoUser(@RequestParam(value = "code") String code) {
        log.info("인가 코드를 이용하여 토큰을 받습니다.");
        String accessToken = authService.getAccessToken(code);
        log.info("토큰에 대한 정보입니다.{}",accessToken);
        Map<String, Object> userInfo = authService.getUserInfo(accessToken);
        log.info("회원 정보 입니다.{}",userInfo);

        String kakaoId = userInfo.get("id").toString();
        String nickName = userInfo.get("nickname").toString();
        String imageUrl = userInfo.get("imageUrl").toString();

        UserResponseDto userResponse = authService.saveUserKakao(kakaoId, nickName, imageUrl);

        String redirectUrl;
        if (!userResponse.isNewUser()){
            redirectUrl = redirectUri + "/signup?userId=" + userResponse.getUserId();
        } else {
            redirectUrl = redirectUri + "/home?userId=" + userResponse.getUserId();
        }
        return "redirect:" + redirectUrl;
    }
}
