package groom_9.BE.Repository;

import groom_9.BE.Domain.Routine;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoutineRepository extends MongoRepository<Routine, ObjectId> {
    List<Routine> findByUserId(ObjectId userId);
    List<Routine> findByName(String name);
    List<Routine> findByTemplateKeyword(String keyword);
    List<Routine> findByIsStopped(boolean isStopped);
}