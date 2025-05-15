package groom_9.BE.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import org.bson.types.ObjectId;

@Document(collection = "records")
public class Record {

    @Id
    private ObjectId id;

    @Field("userId")
    private ObjectId userId;

    @Field("routineId")
    private ObjectId routineId;

    @Field("date")
    private LocalDateTime date;

    @Field("status")
    private String status; // "O", "*", "X" (세모는 별로 바꿨습니다 표현하기 어려울 것 같아서..)
}