package com.poje.remind.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 BAD_REQUEST: 잘못된 요청
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    // 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "허용되지 않은 요청입니다."),
    // 500 INTERNAL_SERVER_ERROR: 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "내부 서버 오류입니다."),

    // Token
    TOKEN_NOT_VALIDATE(HttpStatus.BAD_REQUEST.value(), "올바르지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_MATCHED(HttpStatus.BAD_REQUEST.value(), "리프레시 토큰이 일치하지 않습니다."),

    // Member
    LOGIN_ID_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 아이디입니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "사용자를 찾을 수 없습니다."),
    MEMBER_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요."),

    // Job
    JOB_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "해당 직무를 찾을 수 없습니다."),
    ;


    private final int status;
    private final String message;

}