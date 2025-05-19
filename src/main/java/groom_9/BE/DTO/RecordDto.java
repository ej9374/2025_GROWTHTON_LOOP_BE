package groom_9.BE.DTO;

import lombok.Getter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
public class RecordDto {
    List<ObjectId> records;
}