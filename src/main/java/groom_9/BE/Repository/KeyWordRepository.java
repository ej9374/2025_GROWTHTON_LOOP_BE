package groom_9.BE.Repository;

import groom_9.BE.Domain.KeyWord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface KeyWordRepository extends MongoRepository<KeyWord, String> {
    // userId를 기반으로 해당 유저의 모든 키워드를 찾습니다.
    List<KeyWord> findByUserId(ObjectId userId);

    // content 필드 값이 주어진 문자열과 정확히 일치하는 키워드를 찾습니다.
    Optional<KeyWord> findByContent(String content);

    // content 필드에 특정 문자열을 포함하는 키워드들을 찾습니다. (부분 일치)
    List<KeyWord> findByContentContaining(String keyword);

    // question 필드 값이 주어진 문자열과 정확히 일치하는 키워드를 찾습니다.
    Optional<KeyWord> findByQuestion(String question);

    // question 필드에 특정 문자열을 포함하는 키워드들을 찾습니다. (부분 일치)
    List<KeyWord> findByQuestionContaining(String keyword);

    // 특정 userId에 연결된 루틴(routines 필드에 해당 ObjectId가 있는)을 가진 모든 키워드를 찾습니다.
    List<KeyWord> findByRoutinesContains(ObjectId routineId);

    // 특정 userId를 가진 키워드의 개수를 세어 반환합니다.
    long countByUserId(ObjectId userId);
}
