package groom_9.BE.Service;

import groom_9.BE.Domain.RoutineStatus;
import groom_9.BE.Domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RoutineService {

    private final MongoTemplate mongoTemplate;

    // 홈 화면 키워드 조회
    public String home(ObjectId userId) {
        User user = mongoTemplate.findById(userId, User.class);
        if (user != null && user.getKeyword() != null) {
            return user.getKeyword().getContent();
        } else {
            return "등록된 키워드가 없습니다.";
        }
    }

    // 루틴 성공 기록
    public boolean successRecord(ObjectId userId, List<String> succeedRoutineContents) {
        User user = mongoTemplate.findById(userId, User.class);
        if (user == null || user.getKeyword() == null || user.getKeyword().getRoutines() == null) {
            log.warn("해당 ID({})의 사용자 또는 키워드, 루틴이 없습니다.", userId);
            return false;
        }

        boolean allSuccess = true;
        LocalDateTime now = LocalDateTime.now();

        List<User.EmbeddedRoutine> updatedRoutines = user.getKeyword().getRoutines().stream()
                .map(routine -> {
                    if (succeedRoutineContents.contains(routine.getContent())) {
                        routine.getStatusHistory().add(new User.EmbeddedRoutineStatus(LocalDate.now(), RoutineStatus.SUCCESS));
                        routine.setUpdatedAt(now);
                        log.info("루틴 '{}' 성공 기록 업데이트됨", routine.getContent());
                    }
                    return routine;
                })
                .collect(Collectors.toList());

        user.getKeyword().setRoutines(updatedRoutines);

        try {
            mongoTemplate.save(user);
            log.info("사용자 {}의 루틴 성공 기록 업데이트 완료.", userId);
            return true;
        } catch (Exception e) {
            log.error("사용자 {} 저장 실패: {}", userId, e.getMessage());
            throw new RuntimeException("사용자 정보 저장 실패");
        }
    }

    // 캘린더 데이터 조회 (해당 월의 성공한 루틴 목록)
    public List<SuccessRecordDto> calendar(int thisMonth, ObjectId userId) {
        User user = mongoTemplate.findById(userId, User.class);
        if (user == null || user.getKeyword() == null || user.getKeyword().getRoutines() == null) {
            return Collections.emptyList();
        }

        Month month = Month.of(thisMonth);
        int year = LocalDate.now().getYear();
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        Map<LocalDate, List<String>> routinesByDate = new HashMap<>();

        for (User.EmbeddedRoutine routine : user.getKeyword().getRoutines()) {
            for (User.EmbeddedRoutineStatus status : routine.getStatusHistory()) {
                if (status.getStatus() == RoutineStatus.SUCCESS &&
                        !status.getDate().isBefore(startOfMonth) &&
                        !status.getDate().isAfter(endOfMonth)) {
                    routinesByDate.computeIfAbsent(status.getDate(), k -> new ArrayList<>()).add(routine.getContent());
                }
            }
        }

        List<SuccessRecordDto> calendarData = new ArrayList<>();
        for (Map.Entry<LocalDate, List<String>> entry : routinesByDate.entrySet()) {
            SuccessRecordDto dto = new SuccessRecordDto();
            dto.setDate(entry.getKey());
            dto.setRoutineContents(entry.getValue());
            calendarData.add(dto);
        }

        return calendarData;
    }

    // 월간 보고서 조회 및 피드백 저장
    public MonthlyReportDto getMonthlyReport(int thisMonth, ObjectId userId, String reflectionQuestion, String answer) {
        User user = mongoTemplate.findById(userId, User.class);
        if (user == null || user.getKeyword() == null || user.getKeyword().getRoutines() == null) {
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        }

        Month month = Month.of(thisMonth);
        int year = LocalDate.now().getYear();
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        List<User.EmbeddedRoutine> allRoutinesInMonth = user.getKeyword().getRoutines().stream()
                .filter(routine -> routine.getCreatedAt().toLocalDate().isBefore(endOfMonth.plusDays(1)) &&
                        routine.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .collect(Collectors.toList());

        long totalSuccessCount = user.getKeyword().getRoutines().stream()
                .flatMap(routine -> routine.getStatusHistory().stream())
                .filter(status -> status.getStatus() == RoutineStatus.SUCCESS &&
                        !status.getDate().isBefore(startOfMonth) &&
                        !status.getDate().isAfter(endOfMonth))
                .count();

        int totalRoutines = allRoutinesInMonth.size();
        double averageSuccessRate = totalRoutines > 0 ? (double) totalSuccessCount / totalRoutines * 100.0 : 0.0;

        // 피드백 저장
        if (user.getKeyword().getFeedbacks() == null) {
            user.getKeyword().setFeedbacks(new ArrayList<>());
        }
        user.getKeyword().getFeedbacks().add(new User.EmbeddedFeedback(answer, LocalDateTime.now()));
        user.getKeyword().setQuestion(reflectionQuestion); // 질문 업데이트

        try {
            mongoTemplate.save(user);
            log.info("사용자 {}의 월간 보고서 생성 및 피드백 저장 완료.", userId);
        } catch (Exception e) {
            log.error("사용자 {} 저장 실패: {}", userId, e.getMessage());
            throw new RuntimeException("사용자 정보 저장 실패");
        }

        MonthlyReportDto reportDto = new MonthlyReportDto();
        reportDto.setTotalRoutines(totalRoutines);
        reportDto.setAverageSuccessRate(Math.round(averageSuccessRate * 100.0) / 100.0);
        reportDto.setTotalSuccessCount((int) totalSuccessCount);
        reportDto.setReflectionQuestion(reflectionQuestion);
        reportDto.setAnswer(answer);

        return reportDto;
    }

    // 루틴 수정
    public void updateRoutine(ObjectId userId, String oldContent, String newContent) {
        User user = mongoTemplate.findById(userId, User.class);
        if (user == null || user.getKeyword() == null || user.getKeyword().getRoutines() == null) {
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        }

        List<User.EmbeddedRoutine> updatedRoutines = user.getKeyword().getRoutines().stream()
                .map(routine -> {
                    if (routine.getContent().equals(oldContent)) {
                        routine.setContent(newContent);
                        routine.setUpdatedAt(LocalDateTime.now());
                        log.info("루틴 '{}' -> '{}' 로 수정됨", oldContent, newContent);
                    }
                    return routine;
                })
                .collect(Collectors.toList());

        user.getKeyword().setRoutines(updatedRoutines);

        try {
            mongoTemplate.save(user);
        } catch (Exception e) {
            log.error("사용자 {} 저장 실패: {}", userId, e.getMessage());
            throw new RuntimeException("사용자 정보 저장 실패");
        }
    }

    // 루틴 삭제
    public void deleteRoutine(ObjectId userId, String contentToDelete) {
        User user = mongoTemplate.findById(userId, User.class);
        if (user == null || user.getKeyword() == null || user.getKeyword().getRoutines() == null) {
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        }

        List<User.EmbeddedRoutine> updatedRoutines = user.getKeyword().getRoutines().stream()
                .filter(routine -> !routine.getContent().equals(contentToDelete))
                .collect(Collectors.toList());

        user.getKeyword().setRoutines(updatedRoutines);

        try {
            mongoTemplate.save(user);
            log.info("루틴 '{}' 삭제됨", contentToDelete);
        } catch (Exception e) {
            log.error("사용자 {} 저장 실패: {}", userId, e.getMessage());
            throw new RuntimeException("사용자 정보 저장 실패");
        }
    }

    // 새로운 루틴 추가
    public void addRoutine(ObjectId userId, String newRoutineContent) {
        User user = mongoTemplate.findById(userId, User.class);
        if (user == null || user.getKeyword() == null) {
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        }

        if (user.getKeyword().getRoutines() == null) {
            user.getKeyword().setRoutines(new ArrayList<>());
        }

        User.EmbeddedRoutine newRoutine = new User.EmbeddedRoutine();
        newRoutine.setContent(newRoutineContent);
        newRoutine.setCreatedAt(LocalDateTime.now());
        newRoutine.setStatusHistory(new ArrayList<>()); // 초기 상태 기록 리스트 초기화
        user.getKeyword().getRoutines().add(newRoutine);

        try {
            mongoTemplate.save(user);
            log.info("사용자 {}에게 새로운 루틴 '{}' 추가됨", userId, newRoutineContent);
        } catch (Exception e) {
            log.error("사용자 {} 저장 실패: {}", userId, e.getMessage());
            throw new RuntimeException("사용자 정보 저장 실패");
        }
    }
}