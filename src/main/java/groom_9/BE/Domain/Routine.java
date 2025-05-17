package groom_9.BE.Domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "routine")
@Setter
@Getter
public class Routine {
    @Id
    private ObjectId id;

    @Field("content")
    private List<String> content; //하루1바퀴, 등산하기

    @Field("userId")
    private ObjectId userId;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("successAt")
    private LocalDateTime successAt; //성공한 시간

    @Field("status")
    private RoutineStatus status; //성공 여부
}