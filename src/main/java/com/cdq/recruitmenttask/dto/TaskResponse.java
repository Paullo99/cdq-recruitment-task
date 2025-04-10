package com.cdq.recruitmenttask.dto;

import com.cdq.recruitmenttask.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record TaskResponse(

        @Schema(description = "Unique ID of the task", example = "aaaa-bbbb-cccc-dddd")
        String taskId,

        @Schema(description = "Current status of the task", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "Progress in percent", example = "40")
        Integer progress,

        @Schema(description = "Result of the task", example = "result")
        String result
) {
}