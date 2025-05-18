package groom_9.BE.Service;


import groom_9.BE.DTO.MonthlyReportDto;
import groom_9.BE.DTO.SuccessRecordDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

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

    public List<SuccessRecordDto> calendar(int thisMonth, ObjectId userId) {
        Month month = Month.of(thisMonth);
        int year = LocalDate.now().getYear();

        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        // 해당 유저의 해당 월에 성공한 모든 루틴 조회
        List<Routine> successRoutinesInMonth = routineRepository.findByUserIdAndStatusAndSuccessAtBetween(
                userId, RoutineStatus.SUCCESS, startOfMonth, endOfMonth
        );

        // 날짜별로 루틴 그룹화
        Map<LocalDate, List<String>> routinesByDate = new HashMap<>();
        for (Routine routine : successRoutinesInMonth) {
            if (routine.getSuccessAt() != null) {
                LocalDate successDate = routine.getSuccessAt().toLocalDate();
                String content = String.join(", ", routine.getContent());
                routinesByDate.computeIfAbsent(successDate, k -> new ArrayList<>()).add(content);
            }
        }

        // DTO 리스트로 변환
        List<SuccessRecordDto> calendarData = new ArrayList<>();
        for (Map.Entry<LocalDate, List<String>> entry : routinesByDate.entrySet()) {
            SuccessRecordDto dto = new SuccessRecordDto();
            dto.setDate(entry.getKey());
            dto.setRoutineContents(entry.getValue());
            calendarData.add(dto);
        }

        return calendarData;
    }
    public MonthlyReportDto getMonthlyReport(int thisMonth, ObjectId userId, String reflectionQuestion, String answer) {
        Month month = Month.of(thisMonth);
        int year = LocalDate.now().getYear();

        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        // 해당 유저의 해당 월 전체 루틴 조회
        List<Routine> allRoutinesInMonth = routineRepository.findByUserIdAndCreatedAtBetween(userId, startOfMonth, endOfMonth);

        // 해당 유저의 해당 월 성공한 루틴 조회
        List<Routine> successfulRoutinesInMonth = routineRepository.findByUserIdAndStatusAndSuccessAtBetween(userId, RoutineStatus.SUCCESS, startOfMonth, endOfMonth);

        int totalRoutines = allRoutinesInMonth.size();
        int totalSuccessCount = successfulRoutinesInMonth.size();
        double averageSuccessRate = 0.0;

        if (totalRoutines > 0) {
            averageSuccessRate = (double) totalSuccessCount / totalRoutines * 100.0;
        }

        MonthlyReportDto reportDto = new MonthlyReportDto();
        reportDto.setTotalRoutines(totalRoutines);
        reportDto.setAverageSuccessRate(Math.round(averageSuccessRate * 100.0) / 100.0); // 소수점 둘째 자리까지 반올림
        reportDto.setTotalSuccessCount(totalSuccessCount);
        reportDto.setReflectionQuestion(reflectionQuestion);
        reportDto.setAnswer(answer);

        // 해당 userId를 가진 KeyWord 도큐먼트 찾기
        Optional<KeyWord> existingKeyWord = keyWordRepository.findByUserId(userId).stream().findFirst();

        if (existingKeyWord.isPresent()) {
            // KeyWord 도큐먼트가 존재하면 질문과 답변 업데이트
            KeyWord keyWord = existingKeyWord.get();
            keyWord.setQuestion(reflectionQuestion);
            keyWord.setAnswer(answer); // 답변을 answer 필드에 저장
            keyWordRepository.save(keyWord);
        } else {
            // KeyWord 도큐먼트가 없으면 오류
            log.info("유저가 존재하지 않습니다>");
            throw new RuntimeException("Keyword 문서에 해당 유저가 존재하지 않습니다.");
        }
        return reportDto;
    }


}