package groom_9.BE.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Field("oauthProvider")
    private String oauthProvider;

    @Field("oauthId")
    private String oauthId;

    @Field("onboardingKeyword")
    private List<String> onboardingKeyword;

    @Field("points")
    private Integer points;

    @Field("favoriteRoutines")
    private List<ObjectId> favoriteRoutines;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;
}