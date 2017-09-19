package com.example.plantly.Repository;

public class PlantyRepositoryException extends RuntimeException{
    public PlantyRepositoryException(){

    }

    public PlantyRepositoryException(String message) {
        super(message);
    }

    public PlantyRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlantyRepositoryException(Throwable cause) {
        super(cause);
    }

    public PlantyRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
