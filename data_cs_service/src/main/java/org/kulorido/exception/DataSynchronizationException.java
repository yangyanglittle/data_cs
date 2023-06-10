package org.kulorido.exception;

import lombok.Data;

/**
 * @Author kulorido
 * @Date 2099/12/31 14:17
 * @Version 1.0
 */
@Data
public class DataSynchronizationException extends RuntimeException{

    private String code = "DataSynchronizationException";

    private Throwable throwable;

    public DataSynchronizationException(String message) {
        super(message);
    }

    public DataSynchronizationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DataSynchronizationException(String format, Object...args) {
        super(String.format(format, args));
    }
}
