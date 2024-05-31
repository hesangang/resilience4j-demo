package com.sangang.demoretry.common;

/**
 * @Description:
 * @author: SanGang
 * @author: 2024-05-30 14:51
 */
public class CommonException extends RuntimeException {
    private static final long serialVersionUID = -1L;
    protected String code;

    public CommonException() {
    }

    public CommonException(String message) {
        super(message);
        this.code = CommonCodeEnum.FAIL.getCode();
    }

    public CommonException(String code, String message) {
        super(message);
        this.code = code;
    }

    public CommonException(CommonCodeEnum exceptionCode) {
        this(exceptionCode.getCode(), exceptionCode.getMessage());
    }

    public String getCode() {
        return this.code;
    }
}

