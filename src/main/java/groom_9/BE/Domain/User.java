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
    private String nickname;

    @Field("kakaoId")
    private String kakaoId;

    @Field("imageUrl")
    private String imageUrl;

    @Field("points")
    private Integer points;

    @Field("routines")
    private List<ObjectId> Routines;

    @Field("keywords")
    private List<ObjectId> keywords;

    @Field("age")
    private int age;

    @Field("gender")
    private Gender gender;
}