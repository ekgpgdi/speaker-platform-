package com.dahye.speakerplatform.common.exception.customException;

import com.dahye.speakerplatform.common.enums.ResponseCode;

public class ApplicationException extends RuntimeException {
    public ApplicationException(ResponseCode message) {
        super(message.toString());
    }
}