package com.cdq.recruitmenttask.dto;

import com.cdq.recruitmenttask.model.TaskStatus;

import java.util.List;

public record TaskDetailsResponse(
        String taskId,
        TaskStatus status,
        Integer progress,
        List<FieldChangeResult> result
) {}