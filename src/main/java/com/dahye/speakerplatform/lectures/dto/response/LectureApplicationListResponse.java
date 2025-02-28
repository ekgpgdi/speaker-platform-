package com.dahye.speakerplatform.lectures.dto.response;

import com.dahye.speakerplatform.common.dto.response.AbstractPageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Schema(description = "신청 강연 정보 리스트 응답 모델")
@Getter
@Setter
@SuperBuilder
public class LectureApplicationListResponse extends AbstractPageResponse {
    
    @Schema(description = "신청 강연 정보 리스트")
    private List<LectureApplicationResponse> lectureApplicationList;
}
