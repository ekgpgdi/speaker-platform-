package com.dahye.speakerplatform.lectures.service.applicationSyncService;

import com.dahye.speakerplatform.lectures.domain.Lecture;
import com.dahye.speakerplatform.lectures.service.ApplicationSyncService;
import com.dahye.speakerplatform.lectures.service.LectureApplicationService;
import com.dahye.speakerplatform.lectures.service.LectureService;
import com.dahye.speakerplatform.users.entity.User;
import com.dahye.speakerplatform.users.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ApplicationSyncServiceTest {
    @Mock
    private StringRedisTemplate userApplicationRedisTemplate;

    @Mock
    private UserService userService;

    @Mock
    private LectureService lectureService;

    @Mock
    private LectureApplicationService lectureApplicationService;

    @InjectMocks
    private ApplicationSyncService applicationSyncService;

    @Mock
    private Cursor<String> cursor;

    @Mock
    private SetOperations<String, String> mockSetOperations;

    @Test
    @DisplayName("updateApplicationsToDatabase_Success")
    void testUpdateApplicationsToDatabase() {
        // Given
        String key = "lecture:1:new";
        Mockito.when(userApplicationRedisTemplate.scan(any(ScanOptions.class))).thenReturn(cursor);
        Mockito.when(cursor.hasNext()).thenReturn(true, false); // 한 번만 처리하도록
        Mockito.when(cursor.next()).thenReturn(key);

        Set<String> applicaionEmployeeNoList = new HashSet<>();
        applicaionEmployeeNoList.add("12345");

        Mockito.when(userApplicationRedisTemplate.opsForSet()).thenReturn(mockSetOperations);
        Mockito.when(userApplicationRedisTemplate.opsForSet().members(key)).thenReturn(applicaionEmployeeNoList);

        Optional<Lecture> lecture = Optional.ofNullable(Lecture.builder()
                .id(1L)
                .build());
        Mockito.when(lectureService.getOptional(1L)).thenReturn(lecture);

        Optional<User> user = Optional.ofNullable(User.builder()
                .employeeNo("12345")
                .build());
        Mockito.when(userService.getOptionalByEmployeeNo("12345")).thenReturn(user);

        // When
        applicationSyncService.updateApplicationsToDatabase();

        // Then
        verify(userApplicationRedisTemplate, times(1)).scan(any(ScanOptions.class));
        verify(userApplicationRedisTemplate.opsForSet(), times(1)).add("lecture:1:applications", applicaionEmployeeNoList.toArray(new String[0]));
        verify(userApplicationRedisTemplate.opsForSet(), times(1)).remove(key, applicaionEmployeeNoList.toArray());
    }
}
