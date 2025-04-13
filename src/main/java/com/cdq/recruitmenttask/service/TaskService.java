package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.dto.TaskDetailsResponse;
import com.cdq.recruitmenttask.dto.TaskSummaryResponse;
import com.cdq.recruitmenttask.model.Task;

import java.util.List;

public interface TaskService {

    Task createTask(Long personId);

    TaskDetailsResponse findDetailedTask(String taskId);

    List<TaskSummaryResponse> findAllTaskSummaries();

    Task update(Task task);
}