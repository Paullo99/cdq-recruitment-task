package com.cdq.recruitmenttask.controller;

import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.TaskCreatedResponse;
import com.cdq.recruitmenttask.dto.TaskResponse;
import com.cdq.recruitmenttask.model.Person;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.service.PersonService;
import com.cdq.recruitmenttask.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final TaskService taskService;

    @Operation(
            summary = "Create or update a person",
            description = "Creates or updates a person entity. Triggers asynchronous processing of field differences. Returns a task ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created/updated person and created task",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<TaskCreatedResponse> upsertPerson(@RequestBody PersonRequest personRequest) {
        Person person = Person.builder()
                .name(personRequest.name())
                .surname(personRequest.surname())
                .birthDate(personRequest.birthDate())
                .company(personRequest.company())
                .build();

        Person saved = personService.upsert(person);

        Task task = taskService.createTask(saved.getId());

        return ResponseEntity.ok(new TaskCreatedResponse(task.getId()));
    }
}