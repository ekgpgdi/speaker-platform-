package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.dto.response.ServerResponse;
import com.dahye.speakerplatform.lectures.dto.response.ApplicantUserListResponse;
import com.dahye.speakerplatform.lectures.dto.response.LectureListResponse;
import com.dahye.speakerplatform.lectures.service.LectureApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[ADMIN] LECTURES")
@RestController
@RequestMapping("/admin/api/v1/lectures")
@RequiredArgsConstructor
public class AdminLectureApplicationController {
    private final LectureApplicationService lectureApplicationService;

    @Operation(
            summary = "강연 신청자 목록",
            description = "관리자가 강연의 신청자 목록을 확인합니다."
    )
    @ApiResponse(responseCode = "200", description = "페이지 단위로 강연 신청자 정보가 응답됩니다.",
            content = @Content(schema = @Schema(implementation = LectureListResponse.class)))
    @GetMapping("/{lectureId}/applications")
    public ServerResponse<ApplicantUserListResponse> getLectureApplicantUserList(@PathVariable Long lectureId,
                                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        return ServerResponse.successResponse(
                lectureApplicationService.getLectureApplicantUserList(lectureId, page, size));
    }
}
