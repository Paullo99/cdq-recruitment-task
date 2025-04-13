package com.cdq.recruitmenttask.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record TaskCreatedResponse(
        @Schema(description = "Unique ID of the task", example = "aaaa-bbbb-cccc-dddd")
        String taskId
) {}