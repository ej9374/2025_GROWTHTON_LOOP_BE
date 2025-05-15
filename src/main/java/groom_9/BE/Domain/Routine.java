package groom_9.BE.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import org.bson.types.ObjectId;

@Document(collection = "routines")
public class Routine {

    @Id
    private ObjectId id;

    @Field("name")
    private String name;

    @Field("userId")
    private ObjectId userId;

    @Field("templateKeyword")
    private String templateKeyword;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    @Field("isStopped")
    private Boolean isStopped;

    // 생성자, getter, setter 등
}