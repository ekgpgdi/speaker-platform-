package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.dto.response.ServerResponse;
import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.lectures.dto.response.LectureListResponse;
import com.dahye.speakerplatform.lectures.enums.LectureSort;
import com.dahye.speakerplatform.lectures.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "LECTURES")
@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;

    @Operation(
            summary = "강연 목록 조회",
            description = "사용자가 강연 시작 시간이 `강연 시작 시간 + 1일 >= 현재 시각` 인 강연들을 확인합니다."
    )
    @ApiResponse(responseCode = "200", description = "페이지 단위로 강연 시작 시간이 `강연 시작 시간 + 1일 >= 현재 시각` 인 강연 정보가 응답됩니다.",
            content = @Content(schema = @Schema(implementation = LectureListResponse.class)))
    @GetMapping("")
    public ServerResponse<LectureListResponse> getLectureListByLectureStartTime(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                @RequestParam(value = "sort", defaultValue = "CREATED_AT") LectureSort sort,
                                                                                @RequestParam(value = "sortDirection", defaultValue = "DESC") SortDirection sortDirection) {
        return ServerResponse.successResponse(
                lectureService.getLectureListByLectureStartTime(page, size, sort, sortDirection));
    }
}
