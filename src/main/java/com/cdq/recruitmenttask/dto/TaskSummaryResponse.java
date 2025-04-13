package com.cdq.recruitmenttask.dto;

import com.cdq.recruitmenttask.model.TaskStatus;

public record TaskSummaryResponse(
        String taskId,
        TaskStatus status,
        Integer progress
) {}