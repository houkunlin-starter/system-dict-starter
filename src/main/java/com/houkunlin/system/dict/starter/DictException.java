package com.houkunlin.system.dict.starter;

/**
 * 系统字典异常
 *
 * @author HouKunLin
 */
public class DictException extends Exception {
    public DictException() {
        super();
    }

    public DictException(final String message) {
        super(message);
    }

    public DictException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DictException(final Throwable cause) {
        super(cause);
    }

    protected DictException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
