package groom_9.BE.Service;

import groom_9.BE.DTO.DeleteRoutine;
import groom_9.BE.DTO.KeywordAndRoutineResponseDto;
import groom_9.BE.DTO.SuccessRoutinesDto;
import groom_9.BE.DTO.UserInfoResponseDto;
import groom_9.BE.Domain.RoutineStatus;
import groom_9.BE.Domain.User;
import groom_9.BE.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RoutineService {

    private final UserRepository userRepository;

    public Optional<UserInfoResponseDto> getUserInfo(ObjectId userId) {
        return userRepository.findById(userId)
                .map(UserInfoResponseDto::new);
    }

    // 홈 화면 키워드 조회
    public KeywordAndRoutineResponseDto getKeywordAndRoutines(ObjectId userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String keywordContent = user.getKeyword() != null ? user.getKeyword().getContent() : null;
            List<String> routineContents = user.getKeyword() != null && user.getKeyword().getRoutines() != null
                    ? user.getKeyword().getRoutines().stream()
                    .map(User.EmbeddedRoutine::getContent)
                    .collect(Collectors.toList())
                    : null;
            return new KeywordAndRoutineResponseDto(keywordContent, routineContents);
        } else {
            return new KeywordAndRoutineResponseDto(null, null);
        }
    }


    public void addRoutine(ObjectId objectId, List<String> routineList) {
        Optional<User> userOptional = userRepository.findById(objectId);
        userOptional.ifPresent(user -> {
            List<User.EmbeddedRoutine> newRoutines = routineList.stream()
                    .map(routine -> {
                        User.EmbeddedRoutine embeddedRoutine = new User.EmbeddedRoutine();
                        embeddedRoutine.setContent(routine);
                        embeddedRoutine.setCreatedAt(LocalDateTime.now());
                        embeddedRoutine.setStatus(RoutineStatus.FAIL);
                        return embeddedRoutine;
                    })
                    .collect(Collectors.toList());

            if (user.getKeyword() == null) {
                user.setKeyword(new User.EmbeddedKeyword());
            }
            if (user.getKeyword().getRoutines() == null) {
                user.getKeyword().setRoutines(new java.util.ArrayList<>());
            }
            user.getKeyword().getRoutines().addAll(newRoutines);
            userRepository.save(user);
        });
    }


    public void updateAllRoutinesByContent(ObjectId objectId, String oldContent, String newContent) {
        Optional<User> userOptional = userRepository.findById(objectId);
        log.info("newContent: {}", newContent);
        userOptional.ifPresent(user -> {
            if (user.getKeyword() != null && user.getKeyword().getRoutines() != null) {
                List<User.EmbeddedRoutine> routines = user.getKeyword().getRoutines();
                log.info("updateAllRoutinesByContent: {}", routines.get(0).getContent() + " -> " + newContent);
                boolean found = false;
                for (User.EmbeddedRoutine routine : routines) {
                    if (routine.getContent().equals(oldContent)) {
                        routine.setContent(newContent);
                        routine.setCreatedAt(LocalDateTime.now()); // 수정 시간 업데이트 (선택 사항)
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("기존 루틴 '" + oldContent + "'을 찾을 수 없습니다.");
                } else {
                    userRepository.save(user);
                }
            }
        });
    }


    public boolean saveFeedbacks(ObjectId userId, List<String> answers) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getKeyword() == null) {
                user.setKeyword(new User.EmbeddedKeyword());
            }
            if (user.getKeyword().getFeedbacks() == null) {
                user.getKeyword().setFeedbacks(new java.util.ArrayList<>());
            }

            List<User.EmbeddedFeedback> newFeedbacks = answers.stream()
                    .map(answer -> {
                        User.EmbeddedFeedback feedback = new User.EmbeddedFeedback();
                        feedback.setContent(answer);
                        feedback.setCreatedAt(LocalDateTime.now());
                        return feedback;
                    })
                    .collect(Collectors.toList());

            user.getKeyword().getFeedbacks().addAll(newFeedbacks);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Double> calculateIndividualRoutineSuccessRate(ObjectId objectId) {
        Map<String, Double> routineSuccessRates = new HashMap<>();
        Optional<User> userOptional = userRepository.findById(objectId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getKeyword() != null && user.getKeyword().getRoutines() != null) {
                List<User.EmbeddedRoutine> routines = user.getKeyword().getRoutines();
                Map<String, Integer> successCounts = new HashMap<>();
                LocalDate now = LocalDate.now();
                YearMonth currentMonth = YearMonth.from(now);
                int totalDaysInMonth = currentMonth.lengthOfMonth();

                for (User.EmbeddedRoutine routine : routines) {
                    if (routine.getStatus() == RoutineStatus.SUCCESS && routine.getSuccessAt() != null
                            &&
                            routine.getSuccessAt().toLocalDate().getYear() == now.getYear()
                            &&
                            routine.getSuccessAt().toLocalDate().getMonth() == now.getMonth()) {
                        String content = routine.getContent();
                        successCounts.put(content, successCounts.getOrDefault(content, 0) + 1);
                    }
                }

                for (Map.Entry<String, Integer> entry : successCounts.entrySet()) {
                    String routineContent = entry.getKey();
                    int successCount = entry.getValue();
                    double successRate = (double) successCount / totalDaysInMonth * 100;
                    routineSuccessRates.put(routineContent, successRate);
                }
            }
        }
        return routineSuccessRates;
    }

    public boolean deleteRoutine(ObjectId userId, DeleteRoutine deleteRoutine) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getKeyword() != null && user.getKeyword().getRoutines() != null) {
                List<User.EmbeddedRoutine> existingRoutines = user.getKeyword().getRoutines();
                List<User.EmbeddedRoutine> updatedRoutines = existingRoutines.stream()
                        .filter(routine -> !routine.getContent().equals(deleteRoutine.getRoutine()))
                        .collect(Collectors.toList());

                if (existingRoutines.size() > updatedRoutines.size()) {
                    user.getKeyword().setRoutines(updatedRoutines);
                    userRepository.save(user);
                    return true; // 루틴이 삭제됨
                } else {
                    return false; // 삭제할 루틴을 찾지 못함
                }
            }
        }
        return false; // 사용자 또는 루틴 목록이 없는 경우
    }

    public boolean successRoutine(ObjectId userId, SuccessRoutinesDto successRoutinesDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            log.info("successRoutine: {}", userId);
            User user = userOptional.get();
            if (user.getKeyword() != null && user.getKeyword().getRoutines() != null) {
                List<User.EmbeddedRoutine> existingRoutines = user.getKeyword().getRoutines();
                boolean updated = false;
                log.info("successRoutinesDto.getRoutines(): {}", successRoutinesDto.getRoutines());
                LocalDate today = LocalDate.now();
                for (User.EmbeddedRoutine routine : existingRoutines) {
                    if (successRoutinesDto.getRoutines().contains(routine.getContent()) && routine.getCreatedAt().toLocalDate().equals(today)) {
                        log.info("successRoutine for today: {}", routine.getContent());
                        routine.setStatus(RoutineStatus.SUCCESS);
                        routine.setSuccessAt(LocalDateTime.now());
                        updated = true;
                    }
                }
                if (updated) {
                    log.info("successRoutinesDto.getRoutines() after update: {}", successRoutinesDto.getRoutines());
                    userRepository.save(user);
                    return true;
                }
            }
        }
        return false;
    }
}