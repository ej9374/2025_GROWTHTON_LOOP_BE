package groom_9.BE.Repository;

import groom_9.BE.Domain.Gender;
import groom_9.BE.Domain.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // nickname 필드 값이 주어진 닉네임과 정확히 일치하는 유저를 찾습니다.
    Optional<User> findByNickname(String nickname);

    // kakaoId 필드 값이 주어진 카카오 ID와 정확히 일치하는 유저를 찾습니다.
    Optional<User> findByKakaoId(String kakaoId);

    // points 필드 값이 주어진 값보다 큰 유저들을 찾습니다.
    List<User> findByPointsGreaterThan(Integer points);

    // points 필드 값이 주어진 값보다 작거나 같은 유저들을 찾습니다.
    List<User> findByPointsLessThanEqual(Integer points);

    // 특정 루틴(Routines 필드에 해당 ObjectId가 있는)을 가진 모든 유저를 찾습니다.
    List<User> findByRoutinesContains(ObjectId routineId);

    // 특정 키워드(keywords 필드에 해당 ObjectId가 있는)를 가진 모든 유저를 찾습니다.
    List<User> findByKeywordsContains(ObjectId keywordId);

    // 주어진 나이(age)와 성별(gender)이 모두 일치하는 유저들을 찾습니다.
    List<User> findByAgeAndGender(int age, Gender gender);

    // 주어진 성별(gender)을 가진 모든 유저들을 찾습니다.
    List<User> findByGender(Gender gender);

    // nickname 필드에 특정 문자열을 포함하는 유저들을 찾습니다. (부분 일치)
    List<User> findByNicknameContaining(String keyword);

    // points 필드를 기준으로 내림차순으로 정렬하여 모든 유저를 찾습니다.
    List<User> findAllByOrderByPointsDesc();

    // 특정 나이 범위(fromAge 이상 toAge 이하)에 속하는 유저들을 찾습니다.
    List<User> findByAgeBetween(int fromAge, int toAge);

    // 특정 kakaoId를 가진 유저가 존재하는지 확인합니다.
    boolean existsByKakaoId(String kakaoId);

    // 등록된 유저의 총 수를 반환합니다.
    long count();
}