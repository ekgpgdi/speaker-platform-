package com.dahye.speakerplatform.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime parseToLocalDateTime(String dateTimeStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    public static LocalDateTime parseToLocalDateTime(String dateTimeStr) {
        return parseToLocalDateTime(dateTimeStr, DEFAULT_PATTERN);
    }
}
