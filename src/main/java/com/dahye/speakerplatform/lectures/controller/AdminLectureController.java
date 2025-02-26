package com.dahye.speakerplatform.lectures.controller;

import com.dahye.speakerplatform.common.dto.response.ServerResponse;
import com.dahye.speakerplatform.common.enums.ResponseCode;
import com.dahye.speakerplatform.lectures.dto.request.LectureCreateRequest;
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
}
