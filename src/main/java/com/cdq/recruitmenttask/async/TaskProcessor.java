package com.cdq.recruitmenttask.async;

import com.cdq.recruitmenttask.dto.FieldChangeResult;
import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.error.ApiException;
import com.cdq.recruitmenttask.error.ErrorCode;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.model.TaskStatus;
import com.cdq.recruitmenttask.service.TaskService;
import com.cdq.recruitmenttask.util.FieldComparator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class TaskProcessor {

    private final TaskService taskService;
    private final ObjectMapper objectMapper;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        executor.submit(this::processQueue);
    }

    public void submit(Task task, PersonRequest oldPerson, PersonRequest newPerson) {
        taskQueue.add(() -> executeTask(task, oldPerson, newPerson));
    }

    private void processQueue() {
        while (true) {
            try {
                Runnable job = taskQueue.take();
                job.run();
            } catch (Exception e) {
                throw new ApiException(
                        ErrorCode.TASK_EXECUTION_ERROR,
                        "Error processing task: " + e.getMessage()
                );
            }
        }
    }

    private void executeTask(Task task, PersonRequest oldPerson, PersonRequest newPerson) {
        try {
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setProgress(0);
            taskService.update(task);

            List<String> fields = List.of("name", "surname", "birthDate", "company");
            List<FieldChangeResult> results = new ArrayList<>();

            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);

                String oldVal = extractField(oldPerson, field);
                String newVal = extractField(newPerson, field);

                FieldChangeResult result = FieldComparator.compare(field, oldVal, newVal);
                results.add(result);

                Thread.sleep(2000);

                int progress = ((i + 1) * 100) / fields.size();
                task.setProgress(progress);
                taskService.update(task);
            }

            String jsonResult = objectMapper.writeValueAsString(results);

            task.setProgress(100);
            task.setStatus(TaskStatus.DONE);
            task.setResult(jsonResult);
            taskService.update(task);

        } catch (Exception e) {
            throw new ApiException(
                    ErrorCode.TASK_EXECUTION_ERROR,
                    "Error executing task: " + e.getMessage()
            );
        }
    }

    private String extractField(PersonRequest person, String field) {
        if (person == null) return null;
        return switch (field) {
            case "name" -> person.name();
            case "surname" -> person.surname();
            case "birthDate" -> person.birthDate() != null ? person.birthDate().toString() : null;
            case "company" -> person.company();
            default -> null;
        };
    }
}