package groom_9.BE.Service;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import groom_9.BE.DTO.MemberRequestDto;
import groom_9.BE.DTO.UserDto;
import groom_9.BE.DTO.UserResponseDto;
import groom_9.BE.Domain.Gender;
import groom_9.BE.Domain.User;
import groom_9.BE.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Transactional
    public String getAccessToken(String code){
        String accessToken = "";
        String refreshToken = "";
        String requestUrl = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //필수 헤더 세팅
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            //필수 쿼리 파라미터 세팅
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(clientId);
            sb.append("&redirect_uri=").append(redirectUri);
            sb.append("&code=").append(code);

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            log.info("[KakaoApi.getAccessToken] responseCode = {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while((line = br.readLine()) != null){
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("responseBody = {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return accessToken;
    }

    public Map<String, Object> getUserInfo(String accessToken){
        String host = "https://kapi.kakao.com/v2/user/me";
        Map<String, Object> result = new HashMap<>();
        try {
            URL url = new URL(host);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            urlConnection.setRequestMethod("GET");

            int responseCode = urlConnection.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            String res = "";
            while((line=br.readLine())!=null) {
                res+=line;
            }

            System.out.println("res = " + res);


            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(res);
            JsonObject kakaoAccount = (JsonObject) obj.get("kakao_account");
            JsonObject properties = (JsonObject) obj.get("properties");


            String id = obj.get("id").getAsString();
            String nickname = properties.get("nickname").getAsString();
            String imageUrl = properties.get("profile_image").getAsString();

            String email = null;
            if (kakaoAccount.has("email") && !kakaoAccount.get("email").isJsonNull()) {
                email = kakaoAccount.get("email").getAsString();
            }

            result.put("id", id);
            result.put("nickname", nickname);
            result.put("imageUrl", imageUrl);

            br.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Transactional
    public UserResponseDto saveUserKakao(String kakaoId, String nickname, String imageUrl){
        log.info("saveUserKaKao 호출: kakaoId={}, nickname={}, imageUrl={}", kakaoId, nickname, imageUrl);
        Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);
        if (existingUser.isPresent()){
            log.info("기존 사용자 존재, DB에 저장 x");
            return new UserResponseDto(existingUser.get().getId().toHexString(), false);
        } else {
            User user = User.builder()
                    .kakaoId(kakaoId)
                    .nickname(nickname)
                    .imageUrl(imageUrl)
                    .points(0)
                    .build();
            userRepository.save(user);
            log.info("유저 저장 완료: id={}", user.getId());
            return new UserResponseDto(user.getId().toHexString(), true);
        }
    }

    @Transactional
    public User setMemberInfo(MemberRequestDto memberRequest){
        String userIdStr = memberRequest.getUserId();
        ObjectId userId = new ObjectId(userIdStr);
        Integer age = memberRequest.getAge();
        Gender gender = memberRequest.getGender();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.setAge(age);
        user.setGender(gender);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User findUserInfo(String userId){
        ObjectId id = new ObjectId(userId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return user;
    }

    public void deleteUser(String userId){
        ObjectId id = new ObjectId(userId);
        userRepository.deleteById(id);
    }
}
