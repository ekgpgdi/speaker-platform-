package com.dahye.speakerplatform.lectures.service;

import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.common.exception.customException.ApplicationException;
import com.dahye.speakerplatform.lectures.domain.Application;
import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserListResponse;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureApplicationListResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureApplicationResponse;
import com.dahye.speakerplatform.lectures.enums.LectureApplicationSort;
import com.dahye.speakerplatform.lectures.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LectureApplicationService {
    private final RedisTemplate<String, String> lectureCapacityRedisTemplate;
    private final RedisTemplate<String, String> userApplicationRedisTemplate;
    private final ApplicationRepository applicationRepository;

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

    @Transactional
    public Optional<Application> get(Long applicationId) {
        return applicationRepository.findById(applicationId);
    }

    @Transactional
    public ResponseCode cancel(Long lectureId, Long applicationId) {
        Optional<Application> applicationOptional = get(applicationId);

        if (applicationOptional.isEmpty()) throw new ApplicationException(ResponseCode.NOT_FOUND_APPLICATION);
        applicationRepository.delete(applicationOptional.get());

        String redisKey = "lecture:" + lectureId + ":applications";
        userApplicationRedisTemplate.opsForSet().remove(redisKey, String.valueOf(applicationId));

        int currentCapacity = applicationOptional.get().getLecture().getCurrentCapacity();
        applicationOptional.get().getLecture().setCurrentCapacity(currentCapacity - 1);
        return ResponseCode.SUCCESS;
    }

    @Transactional
    public ResponseCode apply(Long lectureId, String employeeNo) {
        // 1. 신청 가능한 강연인지 - 신청 가능한 시간
        boolean isApplicationTimeValid = isApplicationTimeValid(lectureId);
        if (!isApplicationTimeValid) {
            throw new ApplicationException(ResponseCode.INVALID_LECTURE_TIME);
        }

        // 2. 신청 가능한 강연인지 - 신청 가능한 자리가 남았는지
        boolean isCapacityAvailable = isCapacityAvailable(lectureId);
        if (!isCapacityAvailable) {
            throw new ApplicationException(ResponseCode.NO_CAPACITY_AVAILABLE);
        }

        // 3. 중복 신청 확인
        boolean isAlreadyApplied = isAlreadyApplied(lectureId, employeeNo);
        if (isAlreadyApplied) {
            throw new ApplicationException(ResponseCode.DUPLICATE_APPLICATION);
        }

        // 3. 신청 내역 저장
        ResponseCode responseCode = saveApplication(lectureId, employeeNo);

        if (responseCode != ResponseCode.SUCCESS) {
            throw new ApplicationException(responseCode);
        }

        return ResponseCode.CREATED;
    }

    @Transactional(readOnly = true)
    public LectureApplicationListResponse getLectureApplicationListByLectureStartTime(int page, int size, String employeeNo, LectureApplicationSort sort, SortDirection sortDirection) {
        Page<LectureApplicationResponse> lectureApplicationResponseList = applicationRepository.getLectureApplicationListByLectureStartTime(page, size, employeeNo, sort, sortDirection);
        return LectureApplicationListResponse.builder()
                .lectureApplicationList(lectureApplicationResponseList.getContent())
                .totalElements(lectureApplicationResponseList.getTotalElements())
                .totalPages(lectureApplicationResponseList.getTotalPages())
                .isLast(lectureApplicationResponseList.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ApplicantUserListResponse getLectureApplicantUserList(Long lectureId, int page, int size) {
        Page<ApplicantUserResponse> applicantUserList = applicationRepository.getLectureApplicantUserList(lectureId, PageRequest.of(page, size));

        return ApplicantUserListResponse.builder()
                .applicantUserList(applicantUserList.getContent())
                .totalElements(applicantUserList.getTotalElements())
                .totalPages(applicantUserList.getTotalPages())
                .isLast(applicantUserList.isLast())
                .build();
    }

    @Transactional
    public void save(List<Application> applicationList) {
        applicationRepository.saveAll(applicationList);
    }

    @Transactional
    public void deleteRedis(List<Long> lecturesToDeleteIdList) {
        if (lecturesToDeleteIdList.isEmpty()) {
            return;
        }

        for (Long lectureId : lecturesToDeleteIdList) {
            try {
                // Redis 내 강연 정보 삭제
                String lectureInfoKey = "lecture:" + lectureId + ":startTime";
                lectureCapacityRedisTemplate.delete(lectureInfoKey);

                // Redis 내 강연 신청자 정보 삭제
                String applicationsKey = "lecture:" + lectureId + ":applications";
                userApplicationRedisTemplate.delete(applicationsKey);

                // Redis 내 강연 신청자 새로운 신청 정보 삭제
                String applicationsNewKey = "lecture:" + lectureId + ":applications:new";
                userApplicationRedisTemplate.delete(applicationsNewKey);
            } catch (Exception e) {
                log.error("Failed to delete Redis keys for lectureId: {}, {}", lectureId, e.getMessage());
            }
        }
    }
}
