package com.cdq.recruitmenttask.controller;

import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.PersonResponse;
import com.cdq.recruitmenttask.dto.TaskCreatedResponse;
import com.cdq.recruitmenttask.error.ApiError;
import com.cdq.recruitmenttask.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @Operation(
            summary = "Get all persons",
            description = "Returns a list of all persons",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of persons retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PersonResponse.class))
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<PersonResponse>> getAllTasks() {
        return ResponseEntity.ok(personService.findAll());
    }

    @Operation(
            summary = "Create a person",
            description = "Creates a person entity. Triggers asynchronous processing of field differences. Returns a task ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created/updated person and created task",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskCreatedResponse.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<TaskCreatedResponse> createPerson(@RequestBody @Valid PersonRequest personRequest) {
        TaskCreatedResponse response = personService.createAndProcess(personRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update an existing person",
            description = "Updates an existing person entity by ID. Triggers asynchronous processing of field differences compared to the previous version. Returns a task ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated person and created task",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskCreatedResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Person with the given ID not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskCreatedResponse> updatePerson(@PathVariable Long id, @RequestBody @Valid PersonRequest personRequest) {
        TaskCreatedResponse response = personService.updateAndProcess(id, personRequest);
        return ResponseEntity.ok(response);
    }
}