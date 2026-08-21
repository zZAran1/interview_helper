package com.example.interview_helper_backend.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
public class AiException extends BaseBusinessException {
    public AiException(String msg) {
        super(1004,msg);
    }
}
