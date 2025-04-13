package com.cdq.recruitmenttask.dto;

import com.cdq.recruitmenttask.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public record TaskDetailsResponse(
        @Schema(description = "Unique ID of the task", example = "aaaa-bbbb-cccc-dddd")
        String taskId,

        @Schema(description = "Current status of the task", example = "IN_PROGRESS", enumAsRef = true)
        TaskStatus status,

        @Schema(description = "Progress in percent", example = "40")
        Integer progress,

        @ArraySchema(
                arraySchema = @Schema(description = "Result of the task"),
                schema = @Schema(implementation = FieldChangeResult.class)
        )
        List<FieldChangeResult> result
) {}