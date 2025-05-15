package groom_9.BE.Repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends MongoRepository<groom_9.BE.Domain.Record, ObjectId> {
    List<Record> findByDate(LocalDateTime date);
    List<Record> findByUserId(ObjectId userId);
    List<Record> findByRoutineId(ObjectId routineId);
    List<Record> findByStatus(String status);
    List<Record> findByUserIdAndRoutineId(ObjectId userId, ObjectId routineId);
}