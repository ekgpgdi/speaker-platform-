package com.dahye.speakerplatform.common.exception.customException;

import com.dahye.speakerplatform.common.enums.ResponseCode;

public class NotFoundException extends RuntimeException {
  public NotFoundException(ResponseCode message) {
    super(message.toString());
  }
}
