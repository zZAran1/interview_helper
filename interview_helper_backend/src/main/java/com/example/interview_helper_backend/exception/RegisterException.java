package com.example.interview_helper_backend.exception;


import lombok.Getter;

@Getter
public class RegisterException extends BaseBusinessException {
    public RegisterException(String msg) {
        super(1000,msg);
    }
}
