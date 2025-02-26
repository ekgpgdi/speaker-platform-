package com.dahye.speakerplatform.common.enums;

public enum ResponseCode {
    SUCCESS("성공"),
    SERVER_ERROR("서버 내부 오류"),
    CREATED("생성 성공"),

    // 인증 오류
    UNAUTHORIZED("인증되지 않은 사용자"),
    FORBIDDEN("접근이 금지되었습니다."),

    // USER
    NOT_FOUND_USER("사용자를 찾을 수 없습니다."),

    // invalid
    INVALID_PASSWORD("잘못된 비밀번호입니다."),
    INVALID_TIME_FORMAT("잘못된 데이터 형식입니다."),

    // 필수 값 누락
    REQUIRED_EMPLOYEE_NO("사번은 필수 입력값입니다."),
    REQUIRED_PASSWORD("비밀번호는 필수 입력값입니다."),
    REQUIRED_LECTURER("강연자 정보는 필수 입력값입니다."),
    REQUIRED_LOCATION("강연장 정보는 필수 입력값입니다."),
    REQUIRED_CAPACITY("신청 가능 인원 수는 필수 입력값입니다."),
    REQUIRED_START_TIME("강연 시작 시간은 필수 입력값입니다."),
    REQUIRED_CONTENT("강연 내역 시간은 필수 입력값입니다.");

    public final String message;

    ResponseCode(String message) {
        this.message = message;
    }
}
