package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    Task createTask(Long personId);

    Optional<Task> findById(String id);

    List<Task> findAll();

    Task update(Task task);
}