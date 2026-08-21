package com.example.interview_helper_backend.exception;

public class InterviewException extends BaseBusinessException {
    public InterviewException(String msg) {
        super(1006,msg);
    }
}
