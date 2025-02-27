package com.dahye.speakerplatform.lectures.schedule;

import com.dahye.speakerplatform.lectures.service.LectureApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LectureApplicationScheduler {
    private final LectureApplicationService lectureApplicationService;

    @Scheduled(fixedRate = 60000)
    public void updateApplicationsToDatabase() {
        lectureApplicationService.updateApplicationsToDatabase();
    }
}
