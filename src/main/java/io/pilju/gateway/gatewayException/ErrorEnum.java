package io.pilju.gateway.gatewayException;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    // Common
    INVALID_REQUEST("I001", "Invalid Request"),
    UNAUTHORIZATION("A001", "Access denied"),

    ;
    private String errorCode;
    private String errorMessage;
    ErrorEnum(final String errorCode, final String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
