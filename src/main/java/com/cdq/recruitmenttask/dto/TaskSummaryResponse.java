package com.cdq.recruitmenttask.dto;

import com.cdq.recruitmenttask.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record TaskSummaryResponse(
        @Schema(description = "Task ID", example = "1")
        String taskId,

        @Schema(description = "Current status of the task", example = "PENDING", enumAsRef = true)
        TaskStatus status,

        @Schema(description = "Progress in percent", example = "50")
        Integer progress
) {
}