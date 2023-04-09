package io.pilju.gateway.gatewayException;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
    ErrorResponse(ErrorEnum errorEnum) {
        this.errorCode = errorEnum.getErrorCode();
        this.errorMessage = errorEnum.getErrorMessage();
    }
}
