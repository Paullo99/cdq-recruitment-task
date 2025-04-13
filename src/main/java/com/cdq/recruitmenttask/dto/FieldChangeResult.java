package com.cdq.recruitmenttask.dto;

import com.cdq.recruitmenttask.model.FieldChangeClassification;

public record FieldChangeResult(
        String fieldName,
        String oldValue,
        String newValue,
        FieldChangeClassification classification
) {}
