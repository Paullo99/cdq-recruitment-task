package com.cdq.recruitmenttask.error;

public record ApiError(
        String error,
        String message
) {
}