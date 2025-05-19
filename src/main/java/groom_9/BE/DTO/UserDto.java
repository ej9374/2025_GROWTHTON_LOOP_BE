package groom_9.BE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDto {

    @Schema(description = "사용자 ID (ObjectId의 16진수 문자열)", example = "60c72b2f9f1b2c001c8e4b9a")
    private String id;

    @Schema(description = "kakao 닉네임", example = "사용자")
    private String nickname;

    @Schema(description = "사용자가 직접 입력한 이름(화면에 보여줘야할 이름)", example = "사용자")
    private String name;

    @Schema(description = "카카오 고유 ID", example = "1234567890")
    private String kakaoId;

    @Schema(description = "프로필 이미지 URL", example = "http://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "사용자 포인트", example = "100")
    private Integer points;

    @Schema(description = "사용자 나이", example = "25")
    private int age;

    @Schema(description = "성별", example = "MALE")
    private String gender;

    @Schema(description = "키워드 정보")
    private EmbeddedKeywordDto keyword;


    // User -> UserDto 변환 생성자
    public UserDto(groom_9.BE.Domain.User user) {
        this.id = user.getId() != null ? user.getId().toHexString() : null;
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.kakaoId = user.getKakaoId();
        this.imageUrl = user.getImageUrl();
        this.points = user.getPoints();
        this.age = user.getAge();
        this.gender = user.getGender() != null ? user.getGender().name() : null;

        if (user.getKeyword() != null) {
            this.keyword = new EmbeddedKeywordDto(user.getKeyword());
        }
    }

    @Getter
    @Setter
    public static class EmbeddedKeywordDto {
        @Schema(description = "키워드 내용", example = "건강, 공부")
        private String content;

        @Schema(description = "루틴 리스트")
        private List<EmbeddedRoutineDto> routines;

        @Schema(description = "피드백 리스트")
        private List<EmbeddedFeedbackDto> feedbacks;

        public EmbeddedKeywordDto(groom_9.BE.Domain.User.EmbeddedKeyword keyword) {
            this.content = keyword.getContent();
            if (keyword.getRoutines() != null) {
                this.routines = keyword.getRoutines().stream()
                        .map(EmbeddedRoutineDto::new)
                        .collect(Collectors.toList());
            }
            if (keyword.getFeedbacks() != null) {
                this.feedbacks = keyword.getFeedbacks().stream()
                        .map(EmbeddedFeedbackDto::new)
                        .collect(Collectors.toList());
            }
        }
    }

    @Getter
    @Setter
    public static class EmbeddedRoutineDto {
        @Schema(description = "루틴 내용", example = "하루 1바퀴 걷기")
        private String content;

        @Schema(description = "생성 시간", example = "2025-05-19T15:30:00")
        private LocalDateTime createdAt;

        @Schema(description = "성공 시간", example = "2025-05-19T17:00:00")
        private LocalDateTime successAt;

        @Schema(description = "루틴 상태", example = "FAIL")
        private String status;

        @Schema(description = "루틴과 연관된 이모지", example = "🚶‍♂️")
        private String emoji;

        public EmbeddedRoutineDto(groom_9.BE.Domain.User.EmbeddedRoutine routine) {
            this.content = routine.getContent();
            this.createdAt = routine.getCreatedAt();
            this.successAt = routine.getSuccessAt();
            this.status = routine.getStatus() != null ? routine.getStatus().name() : null;
            this.emoji = routine.getEmoji();
        }
    }

    @Getter
    @Setter
    public static class EmbeddedFeedbackDto {
        @Schema(description = "피드백 내용", example = "잘했어요!")
        private String content;

        @Schema(description = "생성 시간", example = "2025-05-19T16:00:00")
        private LocalDateTime createdAt;

        public EmbeddedFeedbackDto(groom_9.BE.Domain.User.EmbeddedFeedback feedback) {
            this.content = feedback.getContent();
            this.createdAt = feedback.getCreatedAt();
        }
    }
}
