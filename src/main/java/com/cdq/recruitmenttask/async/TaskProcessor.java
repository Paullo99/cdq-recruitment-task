package com.cdq.recruitmenttask.async;

import com.cdq.recruitmenttask.dto.FieldChangeResult;
import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.error.ApiException;
import com.cdq.recruitmenttask.error.ErrorCode;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.model.TaskStatus;
import com.cdq.recruitmenttask.service.TaskService;
import com.cdq.recruitmenttask.util.FieldComparator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskProcessor {
    private static final List<String> FIELDS = List.of("name", "surname", "birthDate", "company");

    private final TaskService taskService;
    private final ObjectMapper objectMapper;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Value("${task.delay.enabled:false}")
    private boolean delayEnabled;

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
                log.error("Error processing task: {}", e.getMessage());
                throw new ApiException(
                        ErrorCode.TASK_EXECUTION_ERROR,
                        "Error processing task: " + e.getMessage()
                );
            }
        }
    }

    private void executeTask(Task task, PersonRequest oldPerson, PersonRequest newPerson) {
        try {
            startTask(task);

            List<FieldChangeResult> results = processFieldDifferences(task, oldPerson, newPerson);

            finalizeTask(task, results);
        } catch (Exception e) {
            handleTaskFailure(task, e);
        }
    }

    private void startTask(Task task) {
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setProgress(0);
        taskService.update(task);
    }

    private List<FieldChangeResult> processFieldDifferences(Task task, PersonRequest oldPerson, PersonRequest newPerson) throws Exception {
        List<FieldChangeResult> results = new ArrayList<>();

        for (int i = 0; i < FIELDS.size(); i++) {
            String field = FIELDS.get(i);

            String oldVal = extractField(oldPerson, field);
            String newVal = extractField(newPerson, field);

            FieldChangeResult result = FieldComparator.compare(field, oldVal, newVal);
            results.add(result);

            if (delayEnabled) {
                Thread.sleep(2000);
            }

            updateProgress(task, i + 1);
        }

        return results;
    }

    private void updateProgress(Task task, int completedFields) {
        int progress = (completedFields * 100) / FIELDS.size();
        task.setProgress(progress);
        taskService.update(task);
    }

    private void finalizeTask(Task task, List<FieldChangeResult> results) throws JsonProcessingException {
        task.setProgress(100);
        task.setStatus(TaskStatus.DONE);
        task.setResult(objectMapper.writeValueAsString(results));
        taskService.update(task);
    }

    private void handleTaskFailure(Task task, Exception e) {
        task.setStatus(TaskStatus.ERROR);
        task.setProgress(0);
        task.setResult(null);
        taskService.update(task);

        log.error("Error executing task: {}", e.getMessage(), e);
        throw new ApiException(
                ErrorCode.TASK_EXECUTION_ERROR,
                "Error processing task: " + e.getMessage()
        );
    }

    private String extractField(PersonRequest person, String field) {
        if (person == null) {
            return null;

        }
        return switch (field) {
            case "name" -> person.name();
            case "surname" -> person.surname();
            case "birthDate" -> person.birthDate() != null ? person.birthDate().toString() : null;
            case "company" -> person.company();
            default -> null;
        };
    }
}