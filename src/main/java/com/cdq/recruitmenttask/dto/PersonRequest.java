package com.cdq.recruitmenttask.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema
public record PersonRequest(
        @Schema(description = "First name", example = "Anna")
        String name,

        @Schema(description = "Last name", example = "Nowak")
        String surname,

        @Schema(description = "Birth date", example = "1999-01-01")
        LocalDate birthDate,

        @Schema(description = "Company", example = "CDQ")
        String company
) {}