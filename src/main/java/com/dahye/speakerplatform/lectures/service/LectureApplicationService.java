package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class LectureApplicationService {
    private final RedisTemplate<String, String> lectureCapacityRedisTemplate;
    private final RedisTemplate<String, String> userApplicationRedisTemplate;

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
        String key = "lecture:" + lectureId + ":applications";
        return userApplicationRedisTemplate.opsForSet().isMember(key, employeeNo);
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
     * 강연 신청 내역 저장 메소드
     *
     * @param lectureId
     * @param employeeNo
     * @return
     */
    @Transactional
    public ResponseCode saveApplication(Long lectureId, String employeeNo) {
        String key = "lecture:" + lectureId + ":applications";
        userApplicationRedisTemplate.opsForSet().add(key, employeeNo);
        return decrementCapacity(lectureId);
    }
}
