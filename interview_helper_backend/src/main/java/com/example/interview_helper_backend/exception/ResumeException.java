package com.example.interview_helper_backend.exception;
import lombok.Getter;


@Getter
public class ResumeException extends BaseBusinessException {
    public ResumeException(String msg) {
        super(1002,msg);
    }
}
