package com.dahye.speakerplatform.lectures.service;

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

    /**
     * 강연 생성 시 강연 신청 좌석과 신청 마감 시간을 Redis에 저장하는 메소드.
     *
     * @param lectureId        강연 ID
     * @param lectureCapacity  강연 신청 가능 좌석 수
     * @param lectureStartTime 강연 신청 마감 시간
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
}
