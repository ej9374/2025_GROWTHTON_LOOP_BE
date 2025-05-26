package groom_9.BE.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
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

    @Field("name")
    private String name;

    @Field("kakaoId")
    private String kakaoId;

    @Field("imageUrl")
    private String imageUrl;

    @Field("points")
    private Integer points;

    @Field("keyword")
    private EmbeddedKeyword keyword;

    @Field("age")
    private int age;

    @Field("gender")
    private Gender gender;


    @Getter
    @Setter
    public static class EmbeddedKeyword {
        @Field("content")
        private String content; // 건강, 공부

        @Field("routines")
        private List<EmbeddedRoutine> routines;

        @Field("feedbacks")
        private List<EmbeddedFeedback> feedbacks;
    }


    @Getter
    @Setter
    public static class EmbeddedRoutine {
        @Field("content")
        private String content; // 하루1바퀴, 등산하기

        @Field("createdAt")
        private LocalDateTime createdAt;

        @Field("successAt")
        private LocalDateTime successAt; // 성공한 시간

        @Field("status")
        private RoutineStatus status; // 성공 여부

        @Field("emoji")
        private String emoji;
    }

    @Getter
    @Setter
    public static class EmbeddedFeedback {
        @Field("content")
        private String content; // 피드백 내용

        @Field("createdAt")
        private LocalDateTime createdAt;
    }
}