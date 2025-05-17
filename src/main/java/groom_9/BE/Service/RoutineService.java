package groom_9.BE.Service;


import groom_9.BE.Domain.KeyWord;
import groom_9.BE.Domain.Routine;
import groom_9.BE.Domain.RoutineStatus;
import groom_9.BE.Repository.KeyWordRepository;
import groom_9.BE.Repository.RoutineRepository;
import groom_9.BE.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final KeyWordRepository keyWordRepository;


    public String home(ObjectId userId) {
        List<KeyWord> byUserId = keyWordRepository.findByUserId(userId);
        if (!byUserId.isEmpty()) {
            KeyWord firstKeyword = byUserId.get(0);
            return firstKeyword.getContent(); // 또는 toString() 등 필요한 값
        } else {
            // 해당 userId를 가진 키워드가 없을 경우의 처리
            return "등록된 키워드가 없습니다."; // 또는 null, 예외 처리 등 적절한 방식
        }
    }

    public boolean successRecord(List<ObjectId> succeedRecords) {
        boolean allSuccess = true;
        for (ObjectId routineId : succeedRecords) {
            Optional<Routine> optionalRoutine = routineRepository.findById(routineId.toString());

            if (optionalRoutine.isPresent()) {
                Routine routine = optionalRoutine.get();
                routine.setStatus(RoutineStatus.SUCCESS);
                routine.setSuccessAt(LocalDateTime.now());
                try {
                    routineRepository.save(routine);
                    log.info("루틴 {} 성공 기록 업데이트됨", routine.getId());
                } catch (Exception e) {
                    log.error("루틴 {} 저장 실패: {}", routine.getId(), e.getMessage());
                    throw new RuntimeException("루틴 저장 실패"); // 예외를 던져 롤백 유도
                }
            } else {
                log.warn("해당 ID({})의 루틴을 찾을 수 없습니다.", routineId);
                allSuccess = false; // 루틴을 찾지 못한 경우 실패로 처리
            }
        }
        if (allSuccess) {
            log.info("선택된 모든 루틴의 성공 기록 업데이트 완료.");
            return true;
        } else {
            log.warn("일부 루틴 기록 업데이트에 실패했습니다.");
            return false;
        }
    }

}