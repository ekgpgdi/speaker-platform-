package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.dto.response.ServerResponse;
import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.common.enums.SortDirection;
import com.dahye.speakerplatform.lectures.dto.request.EmployeeNoRequest;
import com.dahye.speakerplatform.lectures.dto.response.LectureListResponse;
import com.dahye.speakerplatform.lectures.enums.LectureSort;
import com.dahye.speakerplatform.lectures.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @Operation(
            summary = "강연 신청",
            description = "사번을 입력 받아 강연에 신청합니다. 중복 신청은 불가능합니다."
    )
    @ApiResponse(responseCode = "200", description = "강연 신청 성공 ResponseCode = CREATED \n " +
            "강연 신청 실패 \n" +
            "1. 신청 불가능한 시간 = INVALID_LECTURE_TIME \n" +
            "2. 신청 가능한 자리 부족 = NO_CAPACITY_AVAILABLE \n" +
            "3. 중복 신청 = DUPLICATE_APPLICATION",
            content = @Content(schema = @Schema(implementation = ResponseCode.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{lectureId}/applications")
    public ServerResponse<ResponseCode> applyForLecture(@PathVariable Long lectureId,
                                                        @RequestBody EmployeeNoRequest employeeNoRequest) {

        return ServerResponse.successResponse(lectureService.apply(lectureId, employeeNoRequest.getEmployeeNo()));
    }

    @Operation(
            summary = "강연 신청 취소",
            description = "신청 ID 를 통해 강연 신청을 취소합니다."
    )
    @ApiResponse(responseCode = "200", description = "강연 신청 취소 성공 ResponseCode = SUCCESS ",
            content = @Content(schema = @Schema(implementation = ResponseCode.class)))
    @DeleteMapping("/{lectureId}/applications/{applicationId}")
    public ServerResponse<ResponseCode> cancelForLecture(@PathVariable Long lectureId,
                                                         @PathVariable Long applicationId) {

        return ServerResponse.successResponse(lectureService.cancel(lectureId, applicationId));
    }
}
