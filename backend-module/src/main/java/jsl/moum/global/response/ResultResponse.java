package jsl.moum.global.response;

import lombok.Getter;

@Getter
public class ResultResponse {

    private final int status;
    private final String code;
    private final String message;
    private final Object data;

    public static ResultResponse of(ResponseCode responseCode, Object data) {
        return new ResultResponse(responseCode, data);
    }

    public ResultResponse(ResponseCode resultCode, Object data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }
}