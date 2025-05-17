package groom_9.BE.Repository;

import groom_9.BE.Domain.Routine;
import groom_9.BE.Domain.RoutineStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends MongoRepository<Routine, String> {

    // userId를 기반으로 해당 유저의 모든 루틴을 찾습니다.
    List<Routine> findByUserId(ObjectId userId);

    // 특정 상태(status)를 가진 모든 루틴을 찾습니다.
    List<Routine> findByStatus(RoutineStatus status);

    // 특정 userId와 특정 상태(status)를 가진 루틴을 찾습니다.
    List<Routine> findByUserIdAndStatus(ObjectId userId, RoutineStatus status);

    // content 필드에 특정 문자열을 포함하는 루틴들을 찾습니다. (정확한 일치 X)
    List<Routine> findByContentContaining(String keyword);

    // 특정 생성일(createdAt) 이후의 모든 루틴을 찾습니다.
    List<Routine> findByCreatedAtAfter(LocalDateTime dateTime);

    // 특정 성공일(successAt) 이전의 모든 루틴을 찾습니다.
    List<Routine> findBySuccessAtBefore(LocalDateTime dateTime);

    // 특정 userId를 가진 루틴 중에서 성공한 시간(successAt)이 가장 최근인 루틴을 찾습니다.
    Optional<Routine> findTopByUserIdOrderBySuccessAtDesc(ObjectId userId);

    // 특정 userId를 가진 루틴의 개수를 세어 반환합니다.
    long countByUserId(ObjectId userId);

    // 특정 상태가 'SUCCESS'인 루틴의 개수를 세어 반환합니다.
    long countByStatus(RoutineStatus status);

    // MongoDB 쿼리 어노테이션(@Query)을 사용하여 더 복잡한 쿼리를 정의할 수도 있습니다.
    // 예시: 특정 userId의 루틴 중 특정 기간 내에 생성된 루틴 찾기
    @Query("{ 'userId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Routine> findByUserIdAndCreatedAtBetween(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate);

    // 예시: content 배열에 특정 문자열이 정확히 포함된 루틴 찾기
    @Query("{ 'content': ?0 }")
    List<Routine> findByContent(List<String> content);

    List<Routine> findByUserIdAndStatusAndSuccessAtBetween(ObjectId userId, RoutineStatus status, LocalDateTime startOfMonth,LocalDateTime endOfMonth);

}