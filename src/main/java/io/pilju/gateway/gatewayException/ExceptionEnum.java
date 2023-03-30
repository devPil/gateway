package io.pilju.gateway.gatewayException;


import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@Getter
public enum ExceptionEnum {
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "E0001", "RUNTIME_EXCEPTION"),
    ACCESS_DENIED_EXCEPTION(HttpStatus.OK, "E0002", "ACCESS_DENIED_EXCEPTION"),
    INTERNAL_SERVER_ERROR(HttpStatus.OK, "E0003", "INTERNAL_SERVER_ERROR"),
    SECURITY_01(HttpStatus.OK, "S0001", "권한이 없습니다."),
    VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "V0001", "VALIDATION_EXCEPTION"),
    NOT_FOUND_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "T0001", "토큰을 찾을수없습니다."),
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "T0002", "만료된 토큰"),
    INVALID_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "T0003", "잘못된 토큰"),
    NOBODY_EXCEPTION(HttpStatus.OK, "N0001", "잘못된 호출입니다.");

    private final HttpStatus status;
    private final String code;
    private String message;

    ExceptionEnum(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    ExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
