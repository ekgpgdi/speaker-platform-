package com.dahye.speakerplatform.common.enums;

public enum ResponseCode {
    // success
    SUCCESS("Success", false),

    // auth errors
    UNAUTHORIZED("Unauthorized access", true),
    FORBIDDEN("Access forbidden", true),
    SERVER_ERROR("Internal server error", true),

    // not found
    NOT_FOUND_USER("User not found.", true),

    // invalid
    INVALID_PASSWORD("Invalid password.", false);

    public final boolean isFatality;
    public final String message;

    ResponseCode(String message, boolean isFatality) {
        this.message = message;
        this.isFatality = isFatality;
    }
}
