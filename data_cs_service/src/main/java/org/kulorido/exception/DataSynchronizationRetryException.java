package org.kulorido.exception;

public class DataSynchronizationRetryException extends DataSynchronizationException{

    public DataSynchronizationRetryException(String message) {
        super(message);
    }

    public DataSynchronizationRetryException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DataSynchronizationRetryException(String format, Object... args) {
        super(format, args);
    }
}
