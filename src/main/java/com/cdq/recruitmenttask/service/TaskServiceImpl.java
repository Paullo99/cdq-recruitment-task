package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.dto.FieldChangeResult;
import com.cdq.recruitmenttask.dto.TaskDetailsResponse;
import com.cdq.recruitmenttask.dto.TaskSummaryResponse;
import com.cdq.recruitmenttask.error.ApiException;
import com.cdq.recruitmenttask.error.ErrorCode;
import com.cdq.recruitmenttask.mapper.TaskMapper;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.model.TaskStatus;
import com.cdq.recruitmenttask.repository.TaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;
    private final TaskMapper taskMapper;

    @Override
    public Task createTask(Long personId) {
        Task task = Task.builder()
                .status(TaskStatus.PENDING)
                .progress(0)
                .result(null)
                .personId(personId)
                .build();

        return taskRepository.save(task);
    }

    @Override
    @Cacheable(value = "tasks", key = "#taskId")
    public TaskDetailsResponse findDetailedTaskWithCache(String taskId) {
        return findDetailedTask(taskId);
    }

    @CachePut(value = "tasks", key = "#result.taskId", condition = "#result.status.name() == 'DONE'")
    public TaskDetailsResponse findDetailedTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.TASK_NOT_FOUND,
                        "Task with ID " + taskId + " not found."
                ));

        List<FieldChangeResult> results = deserializeResult(task.getResult());
        return new TaskDetailsResponse(task.getId(), task.getStatus(), task.getProgress(), results);
    }

    @Override
    public List<TaskSummaryResponse> findAllTaskSummaries() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toTaskSummaryResponse)
                .toList();
    }

    @Override
    public Task update(Task task) {
        return taskRepository.save(task);
    }

    private List<FieldChangeResult> deserializeResult(String json) {
        if(json == null || json.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to deserialize task result: {}", e.getMessage());
            throw new ApiException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "Failed to deserialize task result: " + e.getMessage()
            );
        }
    }
}