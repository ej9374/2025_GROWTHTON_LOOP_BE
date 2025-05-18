package groom_9.BE.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Setter
@Document(collection = "users")
public class User {
    @Id
    private ObjectId id;

    @Field("nickname")
    private String nickname; // 저장

    @Field("kakaoId")
    private String kakaoId; // 저장

    @Field("imageUrl")
    private String imageUrl; // 가져올 수 있으면 ? < 추가될까요?

    @Field("points")
    private Integer points; // 0으로 설정

    @Field("routines")
    private List<ObjectId> Routines; // Null

    @Field("keywords")
    private List<ObjectId> keywords; //Null

    @Field("age")
    private int age; // MemberRequestDto 입력

    @Field("gender")
    private Gender gender; // MemberRequestDto
}