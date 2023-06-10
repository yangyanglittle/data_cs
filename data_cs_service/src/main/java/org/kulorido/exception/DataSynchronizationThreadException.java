package org.kulorido.exception;

/**
 * @package org.kulorido.exception
 * @Author kulorido
 * @Data 2023/6/8 16:16
 */
public class DataSynchronizationThreadException extends DataSynchronizationException{

    public DataSynchronizationThreadException(String message) {
        super(message);
    }

    public DataSynchronizationThreadException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DataSynchronizationThreadException(String format, Object... args) {
        super(format, args);
    }
}
