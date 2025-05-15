package groom_9.BE.Repository;

import groom_9.BE.Domain.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    List<User> findByNickname(String nickname);
    List<User> findByOnboardingKeyword(String keyword);
    List<User> findByPointsGreaterThanEqual(Integer points);
    // save(), findById(), deleteById(), findAll() 등의 기본 메소드는 MongoRepository에 이미 정의되어 있습니다.
}