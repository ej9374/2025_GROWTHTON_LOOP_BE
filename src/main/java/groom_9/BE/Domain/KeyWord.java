package groom_9.BE.Domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("keyword")
public class KeyWord {

    @Id
    private ObjectId id;

    @Field("userId")
    private ObjectId userId;

    @Field("content")
    private String content; //건강, 공부

    @Field("routines")
    private List<ObjectId> routines;

    @Field("question")
    private String question;
}
