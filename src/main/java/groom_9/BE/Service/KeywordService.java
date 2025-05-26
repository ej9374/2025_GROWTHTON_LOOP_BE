package groom_9.BE.Service;

import groom_9.BE.DTO.RoutineDto;
import groom_9.BE.Domain.RoutineStatus;
import groom_9.BE.Domain.User;
import groom_9.BE.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class KeywordService {

    private final UserRepository userRepository;

    public void setKeyword(String id, String keyword) {
        ObjectId userId = new ObjectId(id);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // keyword 수정시
        if (user.getKeyword() != null) {
            user.getKeyword().setContent(keyword);
        } else {
            // keyword 생성시
            User.EmbeddedKeyword key = new User.EmbeddedKeyword();
            key.setContent(keyword);
            user.setKeyword(key);
        }
        userRepository.save(user);
    }

    public void setRoutine(String id, List<RoutineDto> routineDtoList) {
        ObjectId userId = new ObjectId(id);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getKeyword() == null) {
            throw new IllegalArgumentException("사용자의 키워드가 존재하지 않습니다");
        }
        List<User.EmbeddedRoutine> routineList = new ArrayList<>();
        for (RoutineDto routineDto : routineDtoList) {

            User.EmbeddedRoutine r = new User.EmbeddedRoutine();
            r.setContent(routineDto.getContent());
            r.setEmoji(routineDto.getEmoji());
            r.setCreatedAt(LocalDateTime.now());
            r.setStatus(RoutineStatus.FAIL);
            r.setSuccessAt(null);

            routineList.add(r);
        }
        user.getKeyword().setRoutines(routineList);
        userRepository.save(user);
    }

}
