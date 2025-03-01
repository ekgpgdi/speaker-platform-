package com.dahye.speakerplatform.lectures.schedule;

import com.dahye.speakerplatform.lectures.service.ApplicationSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LectureApplicationScheduler {
    private final ApplicationSyncService applicationSyncService;

    @Scheduled(fixedRate = 60000)
    public void updateApplicationsToDatabase() {
        applicationSyncService.updateApplicationsToDatabase();
    }
}
