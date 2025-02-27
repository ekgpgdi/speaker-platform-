package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.lectures.domain.Application;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.repository.ApplicationRepository;
import com.dahye.speakerplatform.lectures.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class LectureApplicationService {
    private final RedisTemplate<String, String> lectureCapacityRedisTemplate;
    private final RedisTemplate<String, String> userApplicationRedisTemplate;
    private final ApplicationRepository applicationRepository;
    private final LectureRepository lectureRepository;

    /**
     * 강연 생성 시 강연 신청 좌석과 신청 마감 시간을 Redis에 저장하는 메소드
     */
    @Transactional
    public void createLectureRedis(Lecture lecture) {
        int lectureCapacity = lecture.getCapacity();
        LocalDateTime lectureStartTime = lecture.getStartTime();
        String lectureIdStr = Long.toString(lecture.getId());

        log.info("Saving lecture {} to Redis with capacity {}", lectureIdStr, lectureCapacity);
        // 강연 신청 가능 좌석 수를 Redis에 저장
        lectureCapacityRedisTemplate.opsForValue().set("lecture:" + lectureIdStr + ":capacity", String.valueOf(lectureCapacity));

        // 강연 신청 마감 시간 (= 강연 시작 시간) 을 Redis에 저장
        lectureCapacityRedisTemplate.opsForValue().set("lecture:" + lectureIdStr + ":startTime", lectureStartTime.toString());
    }

    /**
     * 신청 가능한 시간 = 강연 시작 전인지 확인하는 메소드
     *
     * @param lectureId
     * @return
     */
    @Transactional
    public boolean isApplicationTimeValid(Long lectureId) {
        String key = "lecture:" + lectureId + ":startTime";

        if (!lectureCapacityRedisTemplate.hasKey(key)) {
            return false;
        }

        String lectureStartTimeStr = lectureCapacityRedisTemplate.opsForValue().get(key);

        LocalDateTime lectureStartTime = LocalDateTime.parse(lectureStartTimeStr);

        return !LocalDateTime.now().isAfter(lectureStartTime);
    }

    /**
     * 중복 신청 내역 확인하는 메소드
     *
     * @param lectureId
     * @param employeeNo
     * @return
     */
    @Transactional
    public boolean isAlreadyApplied(Long lectureId, String employeeNo) {
        // 새로운 신청 내역에서 확인
        String key = "lecture:" + lectureId + ":applications:new";
        if (userApplicationRedisTemplate.opsForSet().isMember(key, employeeNo)) return true;

        // 기존 신청에서 확인
        String existingKey = "lecture:" + lectureId + ":applications";
        return userApplicationRedisTemplate.opsForSet().isMember(existingKey, employeeNo);
    }

    /**
     * 신청 가능한 자리가 남았는지 확인하는 메소드
     *
     * @param lectureId
     * @return
     */
    @Transactional
    public boolean isCapacityAvailable(Long lectureId) {
        String key = "lecture:" + lectureId + ":capacity";

        if (!lectureCapacityRedisTemplate.hasKey(key)) {
            return false;
        }

        String remainingCapacity = lectureCapacityRedisTemplate.opsForValue().get(key);

        int availableSeats = Integer.parseInt(remainingCapacity);
        return availableSeats > 0;
    }

    /**
     * 신청 가능한 좌석 감소시키는 메소드
     *
     * @param lectureId
     * @return
     */
    @Transactional
    public ResponseCode decrementCapacity(Long lectureId) {
        String key = "lecture:" + lectureId + ":capacity";
        // DECRBY 명령어로 원자적 감소
        long remainingCapacity = lectureCapacityRedisTemplate.opsForValue().decrement(key, 1);
        if (remainingCapacity < 0) {
            return ResponseCode.NO_CAPACITY_AVAILABLE;
        }
        return ResponseCode.SUCCESS;
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

    /**
     * 강연 신청 내역 저장 메소드
     *
     * @param lectureId
     * @param employeeNo
     * @return
     */
    @Transactional
    public ResponseCode saveApplication(Long lectureId, String employeeNo) {
        String key = "lecture:" + lectureId + ":applications:new";
        userApplicationRedisTemplate.opsForSet().add(key, employeeNo);
        return decrementCapacity(lectureId);
    }

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
        Optional<Lecture> lectureOptional = lectureRepository.findById(lectureId);

        if (lectureOptional.isEmpty()) {
            log.error("Lecture with ID {} not found", lectureId);
            return false;
        }

        Lecture lecture = lectureOptional.get();

        int batchSize = 100;
        int count = 0;
        List<Application> applicationList = new ArrayList<>();

        for (String applicant : newApplications) {
            userApplicationRedisTemplate.opsForSet().add(existingKey, applicant);

            applicationList.add(Application.builder()
                    .lecture(lecture)
                    .employeeNo(applicant)
                    .build());

            count++;

            if (count >= batchSize) {
                applicationRepository.saveAll(applicationList);
                applicationList.clear();
                count = 0;
            }
        }

        if (!applicationList.isEmpty()) {
            applicationRepository.saveAll(applicationList);
        }
        lecture.setCurrentCapacity(lecture.getCurrentCapacity() + newApplications.size());
        lectureRepository.save(lecture);

        return true;
    }

    @Transactional
    public Optional<Application> get(Long applicationId) {
        return applicationRepository.findById(applicationId);
    }

    @Transactional
    public ResponseCode cancelApplication(Long lectureId, Long applicationId) {
        Optional<Application> applicationOptional = get(applicationId);

        if (applicationOptional.isEmpty()) return ResponseCode.NOT_FOUND_APPLICATION;
        applicationRepository.delete(applicationOptional.get());

        String redisKey = "lecture:" + lectureId + ":applications";
        userApplicationRedisTemplate.opsForSet().remove(redisKey, String.valueOf(applicationId));

        int currentCapacity = applicationOptional.get().getLecture().getCurrentCapacity();
        applicationOptional.get().getLecture().setCurrentCapacity(currentCapacity - 1);
        return ResponseCode.SUCCESS;
    }
}
