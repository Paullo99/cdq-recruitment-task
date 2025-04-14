package com.cdq.recruitmenttask.controller;

import com.cdq.recruitmenttask.dto.TaskDetailsResponse;
import com.cdq.recruitmenttask.dto.TaskSummaryResponse;
import com.cdq.recruitmenttask.error.ApiError;
import com.cdq.recruitmenttask.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Get all tasks",
            description = "Returns a list of all tasks with their status and results.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of tasks retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = TaskSummaryResponse.class))
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<TaskSummaryResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.findAllTaskSummaries());
    }

    @Operation(
            summary = "Get task by ID",
            description = "Returns details of a task with the given ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDetailsResponse> getTaskById(@PathVariable String id) {
        TaskDetailsResponse response = taskService.findDetailedTaskWithCache(id);
        return ResponseEntity.ok(response);
    }
}