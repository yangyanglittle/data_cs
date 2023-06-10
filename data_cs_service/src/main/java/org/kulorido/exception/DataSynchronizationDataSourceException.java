package org.kulorido.exception;

/**
 * @package org.kulorido.exception
 * @Author kulorido
 * @Data 2023/6/9 14:15
 */
public class DataSynchronizationDataSourceException extends DataSynchronizationException{

    public DataSynchronizationDataSourceException(String message) {
        super(message);
    }

    public DataSynchronizationDataSourceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DataSynchronizationDataSourceException(String format, Object... args) {
        super(format, args);
    }
}
