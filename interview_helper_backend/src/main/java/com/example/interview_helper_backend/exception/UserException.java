package com.example.interview_helper_backend.exception;

public class UserException extends BaseBusinessException {
    public UserException(String msg) {
        super(1005,msg);
    }
}
