package com.poje.remind.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicResponse<T> {

    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public BasicResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public BasicResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
