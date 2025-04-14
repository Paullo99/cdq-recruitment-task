package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.dto.FieldChangeResult;
import com.cdq.recruitmenttask.dto.TaskDetailsResponse;
import com.cdq.recruitmenttask.dto.TaskSummaryResponse;
import com.cdq.recruitmenttask.error.ApiException;
import com.cdq.recruitmenttask.model.FieldChangeClassification;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.model.TaskStatus;
import com.cdq.recruitmenttask.repository.TaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private static final String TASK_ID = "123";
    private static final long PERSON_ID = 1L;

    @Test
    void testCreateTask_shouldCreateTaskWithDefaults() {
        Task expected = Task.builder()
                .id(TASK_ID)
                .status(TaskStatus.PENDING)
                .progress(0)
                .result(null)
                .personId(PERSON_ID)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(expected);

        Task task = taskService.createTask(PERSON_ID);

        assertThat(task.getId()).isEqualTo(TASK_ID);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(task.getProgress()).isZero();
        assertThat(task.getPersonId()).isEqualTo(PERSON_ID);
    }

    @Test
    void testFindDetailedTaskWithCache_shouldReturnTaskDetails_whenFound() throws Exception {
        Task task = Task.builder()
                .id(TASK_ID)
                .status(TaskStatus.DONE)
                .progress(100)
                .result("[{}]")
                .build();

        List<FieldChangeResult> results = List.of(new FieldChangeResult("name", "ABCD", "BCD", FieldChangeClassification.MEDIUM));

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(objectMapper.readValue(eq("[{}]"), ArgumentMatchers.<TypeReference<List<FieldChangeResult>>>any())).thenReturn(results);

        TaskDetailsResponse response = taskService.findDetailedTaskWithCache(TASK_ID);

        assertThat(response.taskId()).isEqualTo(TASK_ID);
        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
        assertThat(response.progress()).isEqualTo(100);
        assertThat(response.result()).isEqualTo(results);
    }

    @Test
    void testFindDetailedTaskWithCache_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findDetailedTaskWithCache(TASK_ID))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Task with ID 123 not found.");
    }

    @Test
    void testFindDetailedTaskWithCache_shouldReturnEmptyResultList_whenJsonIsNull() {
        Task task = Task.builder().id(TASK_ID).result(null).build();
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));

        TaskDetailsResponse response = taskService.findDetailedTaskWithCache(TASK_ID);

        assertThat(response.result()).isEmpty();
    }

    @Test
    void testFindDetailedTaskWithCache_shouldThrowApiExceptionOnDeserializationError() throws Exception {
        Task task = Task.builder().id(TASK_ID).result("broken").build();

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(objectMapper.readValue(anyString(), ArgumentMatchers.<TypeReference<List<FieldChangeResult>>>any()))
                .thenThrow(new RuntimeException("deserialization error"));

        assertThatThrownBy(() -> taskService.findDetailedTaskWithCache(TASK_ID))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Failed to deserialize");
    }

    @Test
    void shouldReturnAllTasks() {
        List<Task> tasks = List.of(new Task(), new Task());
        when(taskRepository.findAll()).thenReturn(tasks);

        List<TaskSummaryResponse> result = taskService.findAllTaskSummaries();
        assertThat(result).hasSize(2);
    }

    @Test
    void testUpdate_shouldUpdateTask() {
        Task input = Task.builder().id(TASK_ID).status(TaskStatus.IN_PROGRESS).build();
        when(taskRepository.save(input)).thenReturn(input);

        Task updated = taskService.update(input);
        assertThat(updated.getId()).isEqualTo(TASK_ID);
    }
}
