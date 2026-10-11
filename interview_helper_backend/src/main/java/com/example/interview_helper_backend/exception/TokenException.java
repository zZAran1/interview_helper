package com.example.interview_helper_backend.exception;
import lombok.Getter;


@Getter
public class TokenException extends BaseBusinessException{

    public TokenException(String msg) {
        super(1003,msg);

    }
}
