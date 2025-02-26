package com.dahye.speakerplatform.common.enums;

import org.springframework.data.domain.Sort;

public enum SortDirection {
    ASC(Sort.Direction.ASC),
    DESC(Sort.Direction.DESC);

    private final Sort.Direction direction;

    SortDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    public Sort.Direction getDirection() {
        return direction;
    }
}
