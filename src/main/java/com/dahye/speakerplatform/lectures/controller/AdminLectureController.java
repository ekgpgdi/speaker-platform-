package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.dto.response.ServerResponse;
import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.lectures.dto.request.LectureCreateRequest;
import com.dahye.speakerplatform.lectures.dto.response.LectureListResponse;
import com.dahye.speakerplatform.lectures.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[ADMIN] LECTURES")
@RestController
@RequestMapping("/admin/api/v1/lectures")
@RequiredArgsConstructor
public class AdminLectureController {
    private final LectureService lectureService;

    @Operation(
            summary = "강연 생성",
            description = "관리자가 새로운 강연을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "성공적으로 강연 내용이 생성되었습니다. ResponseCode = CREATED ",
            content = @Content(schema = @Schema(implementation = ResponseCode.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public ServerResponse<ResponseCode> createLecture(
            @Valid @RequestBody LectureCreateRequest lectureCreateRequest) {

        return ServerResponse.successResponse(
                lectureService.createLecture(lectureCreateRequest));
    }

    @Operation(
            summary = "강연 목록 조회",
            description = "관리자가 강연 목록을 확인합니다."
    )
    @ApiResponse(responseCode = "200", description = "페이지 단위로 강연 정보가 응답됩니다. \n" +
            "사용 가능한 sort 값 : capacity, currentCapacity, startTime, createdAt \n " +
            "사용 가능한 direction 값 : asc, desc",
            content = @Content(schema = @Schema(implementation = LectureListResponse.class)))
    @GetMapping("")
    public ServerResponse<LectureListResponse> getLectureList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                                              @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
                                                              @RequestParam(value = "direction", defaultValue = "desc") String direction) {
        return ServerResponse.successResponse(
                lectureService.getLectureList(page, size, sort, direction));
    }
}
