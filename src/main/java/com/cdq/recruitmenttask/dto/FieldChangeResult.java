package com.cdq.recruitmenttask.dto;

import com.cdq.recruitmenttask.model.FieldChangeClassification;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record FieldChangeResult(

        @Schema(description = "Name of the field that changed", example = "surname")
        String fieldName,

        @Schema(description = "Previous value of the field", example = "Anna")
        String oldValue,

        @Schema(description = "New value of the field", example = "Nowak")
        String newValue,

        @Schema(description = "Classification of the change based on similarity", example = "MEDIUM", enumAsRef = true)
        FieldChangeClassification classification
) {}
