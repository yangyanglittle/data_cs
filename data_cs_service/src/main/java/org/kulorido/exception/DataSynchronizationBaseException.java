package org.kulorido.exception;

public class DataSynchronizationBaseException {


    public static class DataSynchronizationCoreException extends RuntimeException{
        public DataSynchronizationCoreException(){
            super("CPU内核不可为0");
        }
    }
}
