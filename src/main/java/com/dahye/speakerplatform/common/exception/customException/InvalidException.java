package com.dahye.speakerplatform.common.exception.customException;

import com.dahye.speakerplatform.common.enums.ResponseCode;

public class InvalidException extends RuntimeException {
  public InvalidException(ResponseCode message) {
    super(message.toString());
  }
}
