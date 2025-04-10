package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.model.TaskStatus;
import com.cdq.recruitmenttask.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

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
    public Optional<Task> findById(String id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task update(Task task) {
        return taskRepository.save(task);
    }
}