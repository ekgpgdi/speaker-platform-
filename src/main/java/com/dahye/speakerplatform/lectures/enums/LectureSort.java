package com.dahye.speakerplatform.lectures.enums;

public enum LectureSort {
    CAPACITY("capacity"),
    CURRENT_CAPACITY("currentCapacity"),
    START_TIME("startTime"),
    CREATED_AT("createdAt");

    private final String fieldName;

    LectureSort(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
