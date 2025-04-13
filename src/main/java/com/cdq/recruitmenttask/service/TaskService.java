package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.dto.TaskDetailsResponse;
import com.cdq.recruitmenttask.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    Task createTask(Long personId);

    Optional<TaskDetailsResponse> findDetailedTask(String taskId);

    List<Task> findAll();

    Task update(Task task);
}