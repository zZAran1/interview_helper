package com.example.interview_helper_backend.exception;

import lombok.Getter;

@Getter
public class LoginException extends BaseBusinessException {
    public LoginException(String msg) {
        super(1001,msg);
    }
}
