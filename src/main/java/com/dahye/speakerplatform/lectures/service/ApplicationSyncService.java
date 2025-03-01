package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.lectures.domain.Application;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.users.entity.User;
import com.dahye.speakerplatform.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationSyncService {
    private final RedisTemplate<String, String> userApplicationRedisTemplate;
    private final UserService userService;
    private final LectureService lectureService;
    private final LectureApplicationService lectureApplicationService;

    /**
     * redis 에서 신규 신청 사용자 정보 DB 저장
     */
    public void updateApplicationsToDatabase() {
        // :new로 끝나는 모든 Redis 키를 가져옴
        Cursor<String> cursor = userApplicationRedisTemplate.scan(ScanOptions.scanOptions().match("lecture:*:new").build());

        // 키를 순차적으로 처리
        while (cursor.hasNext()) {
            String key = cursor.next();
            redisToDatabase(key);
        }
        cursor.close();
    }

    /**
     * lectureId 추출
     *
     * @param key
     * @return
     */
    private Long extractLectureIdFromKey(String key) {
        String[] parts = key.split(":");
        return Long.parseLong(parts[1]);  // lectureId가 두 번째 위치에 존재
    }

    @Transactional
    public void redisToDatabase(String key) {
        Long lectureId = extractLectureIdFromKey(key);
        Set<String> newApplications = userApplicationRedisTemplate.opsForSet().members(key);

        if (!newApplications.isEmpty()) {
            if (saveApplicationsToDatabase(lectureId, newApplications)) {
                userApplicationRedisTemplate.opsForSet().remove(key, newApplications.toArray());
            }
        }
    }

    @Transactional
    public boolean saveApplicationsToDatabase(Long lectureId, Set<String> newApplications) {
        String existingKey = "lecture:" + lectureId + ":applications";
        Optional<Lecture> lectureOptional = lectureService.getOptional(lectureId);

        if (lectureOptional.isEmpty()) {
            log.error("Lecture with ID {} not found", lectureId);
            return false;
        }

        Lecture lecture = lectureOptional.get();

        int batchSize = 100;
        int count = 0;
        List<Application> applicationList = new ArrayList<>();
        List<String> applicaionEmployeeNoList = new ArrayList<>();

        for (String employeeNo : newApplications) {
            Optional<User> userOptional = userService.getOptionalByEmployeeNo(employeeNo);

            if (userOptional.isEmpty()) {
                log.error("User with Employee No {} not found", employeeNo);
                continue;
            }

            applicationList.add(Application.builder()
                    .lecture(lecture)
                    .user(userOptional.get())
                    .build());
            applicaionEmployeeNoList.add(employeeNo);

            count++;

            if (count >= batchSize) {
                lectureApplicationService.save(applicationList);
                applicationList.clear();
                count = 0;
            }
        }

        if (!applicationList.isEmpty()) {
            lectureApplicationService.save(applicationList);
        }

        userApplicationRedisTemplate.opsForSet().add(existingKey, applicaionEmployeeNoList.toArray(new String[0]));
        lectureService.addCapacity(lecture, newApplications.size());

        return true;
    }


}
