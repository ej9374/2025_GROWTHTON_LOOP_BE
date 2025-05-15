package groom_9.BE.Repository;

import groom_9.BE.Domain.Reward;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RewardRepository extends MongoRepository<Reward, ObjectId> {
    List<Reward> findByName(String name);
    List<Reward> findByCostLessThanEqual(Integer cost);
    List<Reward> findByDescriptionContaining(String keyword);
}