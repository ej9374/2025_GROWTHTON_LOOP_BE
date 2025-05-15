package groom_9.BE.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;
import org.bson.types.ObjectId;

@Document(collection = "reports")
public class Report {

    @Id
    private ObjectId id;

    @Field("userId")
    private ObjectId userId;

    @Field("periodStart")
    private LocalDateTime periodStart;

    @Field("periodEnd")
    private LocalDateTime periodEnd;

    @Field("totalRoutines")
    private Integer totalRoutines;

    @Field("averageCompletionRate")
    private Double averageCompletionRate;

    @Field("cumulativeSuccessCount")
    private Integer cumulativeSuccessCount;

    @Field("reflection")
    private Map<String, String> reflection; // 예: {"nextWeekRoutine": "명상 추가", "proudRoutine": "매일 스트레칭"}

    @Field("createdAt")
    private LocalDateTime createdAt;

    // 생성자, getter, setter 등
}