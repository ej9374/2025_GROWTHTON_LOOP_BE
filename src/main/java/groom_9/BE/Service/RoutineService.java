package groom_9.BE.Service;

import groom_9.BE.DTO.MonthlyReportDto;
import groom_9.BE.DTO.SuccessRecordDto;
import groom_9.BE.Domain.RoutineStatus;
import groom_9.BE.Domain.User;
import groom_9.BE.Repository.UserRepository;
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

    private final UserRepository userRepository;

    // 홈 화면 키워드 조회
    public String home(ObjectId userId) {
        return userRepository.findById(userId)
                .map(user -> user.getKeyword() != null ? user.getKeyword().getContent() : "등록된 키워드가 없습니다.")
                .orElse("등록된 키워드가 없습니다.");
    }

    // 루틴 성공 기록
    public boolean successRecord(ObjectId userId, List<String> succeedRoutineContents) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getKeyword() == null || user.getKeyword().getRoutines() == null) {
                        log.warn("해당 ID({})의 사용자 또는 키워드, 루틴이 없습니다.", userId);
                        return false;
                    }

                    LocalDateTime now = LocalDateTime.now();
                    boolean anySuccess = false;

                    List<User.EmbeddedRoutine> updatedRoutines = user.getKeyword().getRoutines().stream()
                            .map(routine -> {
                                if (succeedRoutineContents.contains(routine.getContent())) {
                                    routine.setStatus(RoutineStatus.SUCCESS);
                                    routine.setSuccessAt(now);
                                    log.info("루틴 '{}' 성공 기록 업데이트됨", routine.getContent());
                                    return routine;
                                }
                                return routine;
                            })
                            .collect(Collectors.toList());

                    user.getKeyword().setRoutines(updatedRoutines);

                    try {
                        userRepository.save(user);
                        log.info("사용자 {}의 루틴 성공 기록 업데이트 완료.", userId);
                        return true;
                    } catch (Exception e) {
                        log.error("사용자 {} 저장 실패: {}", userId, e.getMessage());
                        throw new RuntimeException("사용자 정보 저장 실패");
                    }
                })
                .orElseGet(() -> {
                    log.warn("해당 ID({})의 사용자를 찾을 수 없습니다.", userId);
                    return false;
                });
    }

    // 캘린더 데이터 조회 (해당 월의 성공한 루틴 목록)
    public List<SuccessRecordDto> calendar(int thisMonth, ObjectId userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getKeyword() == null || user.getKeyword().getRoutines() == null) {
                        return Collections.emptyList();
                    }

                    Month month = Month.of(thisMonth);
                    int year = LocalDate.now().getYear();
                    LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
                    LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

                    Map<LocalDate, List<String>> routinesByDate = new HashMap<>();

                    for (User.EmbeddedRoutine routine : user.getKeyword().getRoutines()) {
                        if (routine.getStatus() == RoutineStatus.SUCCESS &&
                                routine.getSuccessAt() != null &&
                                !routine.getSuccessAt().isBefore(startOfMonth) &&
                                !routine.getSuccessAt().isAfter(endOfMonth)) {
                            LocalDate successDate = routine.getSuccessAt().toLocalDate();
                            routinesByDate.computeIfAbsent(successDate, k -> new ArrayList<>()).add(routine.getContent());
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
                })
                .orElse(Collections.emptyList());
    }

    // 월간 보고서 조회 및 피드백 저장
    public MonthlyReportDto getMonthlyReport(int thisMonth, ObjectId userId, String reflectionQuestion, String answer) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getKeyword() == null || user.getKeyword().getRoutines() == null) {
                        throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
                    }

                    Month month = Month.of(thisMonth);
                    int year = LocalDate.now().getYear();
                    LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
                    LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

                    List<User.EmbeddedRoutine> allRoutinesInMonth = user.getKeyword().getRoutines().stream()
                            .filter(routine -> routine.getCreatedAt().isBefore(endOfMonth) &&
                                    routine.getCreatedAt().isAfter(startOfMonth))
                            .collect(Collectors.toList());

                    long totalSuccessCount = user.getKeyword().getRoutines().stream()
                            .filter(routine -> routine.getStatus() == RoutineStatus.SUCCESS &&
                                    routine.getSuccessAt() != null &&
                                    !routine.getSuccessAt().isBefore(startOfMonth) &&
                                    !routine.getSuccessAt().isAfter(endOfMonth))
                            .count();

                    int totalRoutines = allRoutinesInMonth.size();
                    double averageSuccessRate = totalRoutines > 0 ? (double) totalSuccessCount / totalRoutines * 100.0 : 0.0;

                    // 피드백 저장
                    if (user.getKeyword().getFeedbacks() == null) {
                        user.getKeyword().setFeedbacks(new ArrayList<>());
                    }
                    user.getKeyword().getFeedbacks().add(new User.EmbeddedFeedback(answer, LocalDateTime.now()));
                    user.getKeyword().setFeedbacks(reflectionQuestion); // 질문 업데이트

                    try {
                        userRepository.save(user);
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
                })
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
    }

    // 루틴 수정
    public void updateRoutine(ObjectId userId, String oldContent, String newContent) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    if (user.getKeyword() != null && user.getKeyword().getRoutines() != null) {
                        List<User.EmbeddedRoutine> updatedRoutines = user.getKeyword().getRoutines().stream()
                                .map(routine -> {
                                    if (routine.getContent().equals(oldContent)) {
                                        routine.setContent(newContent);
                                        log.info("루틴 '{}' -> '{}' 로 수정됨", oldContent, newContent);
                                    }
                                    return routine;
                                })
                                .collect(Collectors.toList());
                        user.getKeyword().setRoutines(updatedRoutines);
                        userRepository.save(user);
                    } else {
                        throw new RuntimeException("해당 사용자의 키워드 또는 루틴이 없습니다.");
                    }
                }, () -> {
                    throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
                });
    }

    // 루틴 삭제
    public void deleteRoutine(ObjectId userId, String contentToDelete) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    if (user.getKeyword() != null && user.getKeyword().getRoutines() != null) {
                        List<User.EmbeddedRoutine> updatedRoutines = user.getKeyword().getRoutines().stream()
                                .filter(routine -> !routine.getContent().equals(contentToDelete))
                                .collect(Collectors.toList());
                        user.getKeyword().setRoutines(updatedRoutines);
                        userRepository.save(user);
                        log.info("루틴 '{}' 삭제됨", contentToDelete);
                    } else {
                        throw new RuntimeException("해당 사용자의 키워드 또는 루틴이 없습니다.");
                    }
                }, () -> {
                    throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
                });
    }

    // 새로운 루틴 추가
    public void addRoutine(ObjectId userId, String newRoutineContent) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    if (user.getKeyword() == null) {
                        user.setKeyword(new User.EmbeddedKeyword());
                    }
                    if (user.getKeyword().getRoutines() == null) {
                        user.getKeyword().setRoutines(new ArrayList<>());
                    }

                    User.EmbeddedRoutine newRoutine = new User.EmbeddedRoutine();
                    newRoutine.setContent(newRoutineContent);
                    newRoutine.setCreatedAt(LocalDateTime.now());
                    user.getKeyword().getRoutines().add(newRoutine);

                    userRepository.save(user);
                    log.info("사용자 {}에게 새로운 루틴 '{}' 추가됨", userId, newRoutineContent);
                }, () -> {
                    throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
                });
    }
}