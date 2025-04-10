package com.cdq.recruitmenttask.async;

import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.model.TaskStatus;
import com.cdq.recruitmenttask.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskProcessor {

    private final TaskService taskService;

    @Async
    public void process(Task task) {
        try {
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setProgress(0);
            taskService.update(task);

            for (int i = 1; i <= 10; i++) {
                Thread.sleep(1000);
                Task current = taskService.findById(task.getId()).orElseThrow();
                current.setProgress(i * 10);
                taskService.update(current);
            }

            Task finalTask = taskService.findById(task.getId()).orElseThrow();
            finalTask.setProgress(100);
            finalTask.setStatus(TaskStatus.DONE);
            finalTask.setResult("{}");
            taskService.update(finalTask);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}