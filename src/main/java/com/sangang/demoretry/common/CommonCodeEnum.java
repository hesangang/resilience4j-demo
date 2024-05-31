package com.sangang.demoretry.common;

/**
 * @Description:
 * @author: SanGang
 * @author: 2024-05-30 14:52
 */
public enum CommonCodeEnum {
    SUCCESS("0", "ok"),
    FAIL("-1", "fail"),
    BAD_REQUEST("400", "BadRequest"),
    UNAUTHORIZED("401", "Unauthorized");

    private final String code;
    private final String message;

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private CommonCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

