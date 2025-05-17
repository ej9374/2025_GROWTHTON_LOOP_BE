package groom_9.BE.Repository;

import groom_9.BE.Domain.Routine;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoutineRepository extends MongoRepository<Routine, ObjectId> {
}