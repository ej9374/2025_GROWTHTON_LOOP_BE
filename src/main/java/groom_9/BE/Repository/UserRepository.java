package groom_9.BE.Repository;

import groom_9.BE.Domain.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
//    List<User> findByNickname(String nickname);
//    List<User> findByKeyword(String keyword);
//    List<User> findByPointsGreaterThanEqual(Integer points);
    Optional<User> findById(ObjectId id);
    Optional<User> findByKakaoId(String kakaoId);
//    // save(), findById(), deleteById(), findAll() 등의 기본 메소드는 MongoRepository에 이미 정의되어 있습니다.
}