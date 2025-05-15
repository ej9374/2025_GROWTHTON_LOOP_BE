package groom_9.BE.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import org.bson.types.ObjectId;

@Document(collection = "rewards")
public class Reward {

    @Id
    private ObjectId id;

    @Field("name")
    private String name;

    @Field("cost")
    private Integer cost;

    @Field("description")
    private String description;

    // 생성자, getter, setter 등
}