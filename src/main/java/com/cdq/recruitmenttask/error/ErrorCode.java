package com.cdq.recruitmenttask.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PERSON_NOT_FOUND(HttpStatus.NOT_FOUND),
    TASK_EXECUTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus httpStatus;
}