package groom_9.BE.DTO;


import groom_9.BE.Domain.Gender;
import groom_9.BE.Domain.User;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class UserInfoResponseDto {
    private ObjectId id;
    private String nickname;
    private String kakaoId;
    private String imageUrl;
    private Integer points;
    private User.EmbeddedKeyword keyword;
    private int age;
    private Gender gender;

    public UserInfoResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.kakaoId = user.getKakaoId();
        this.imageUrl = user.getImageUrl();
        this.points = user.getPoints();
        this.keyword = user.getKeyword();
        this.age = user.getAge();
        this.gender = user.getGender();
    }
}