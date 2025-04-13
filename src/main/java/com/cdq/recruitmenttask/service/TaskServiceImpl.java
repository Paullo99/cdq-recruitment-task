package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.dto.FieldChangeResult;
import com.cdq.recruitmenttask.dto.TaskDetailsResponse;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.model.TaskStatus;
import com.cdq.recruitmenttask.repository.TaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;

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
    public Optional<TaskDetailsResponse> findDetailedTask(String taskId) {
        return taskRepository.findById(taskId).map(task -> {
            List<FieldChangeResult> results = deserializeResult(task.getResult());
            return new TaskDetailsResponse(task.getId(), task.getStatus(), task.getProgress(), results);
        });
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task update(Task task) {
        return taskRepository.save(task);
    }

    private List<FieldChangeResult> deserializeResult(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}