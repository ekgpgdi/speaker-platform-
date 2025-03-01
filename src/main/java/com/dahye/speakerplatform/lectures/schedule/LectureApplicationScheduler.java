package com.dahye.speakerplatform.lectures.schedule;

import com.dahye.speakerplatform.lectures.service.ApplicationSyncService;
import com.dahye.speakerplatform.lectures.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LectureApplicationScheduler {
    private final ApplicationSyncService applicationSyncService;
    private final LectureService lectureService;

    /**
     *  1분마다 Redis 에 들어온 신규 신청자 정보를 DB 에 저장
     */
    @Scheduled(fixedRate = 60000)
    public void updateApplicationsToDatabase() {
        applicationSyncService.updateApplicationsToDatabase();
    }

    /**
     * 10분마다 강연 시작 시간이 1시간이 지난 강연의 신청자 정보를 Redis 에서 삭제
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void cleanUpOldLectures() {
        lectureService.cleanUpOldLectures();
    }
}
