package com.cdq.recruitmenttask.async;

import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.model.TaskStatus;
import com.cdq.recruitmenttask.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskProcessorTest {

    @Mock
    private TaskService taskService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TaskProcessor taskProcessor;

    private final String TASK_ID = "123";
    private final long PERSON_ID = 1L;

    private final Task task = Task.builder().id(TASK_ID).personId(PERSON_ID).build();
    private final PersonRequest request = new PersonRequest("Anna", "Nowak", java.time.LocalDate.of(1999, 1, 1), "CDQ");

    @BeforeEach
    void setUp() {
        taskProcessor.init();
    }

    @Test
    void testSubmit_shouldProcessSubmittedTask() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        taskProcessor.submit(task, null, request);

        Thread.sleep(500);

        verify(taskService, atLeastOnce()).update(any(Task.class));
        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    void testSubmit_shouldNotThrow_whenFieldIsMissing() throws Exception {
        PersonRequest partial = new PersonRequest(null, null, null, null);
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        taskProcessor.submit(task, null, partial);

        Thread.sleep(500);

        verify(taskService, atLeastOnce()).update(any(Task.class));
    }

    @Test
    void testSubmit_shouldHandleExceptionInSerialization() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("serialization failed"));

        assertThatCode(() -> taskProcessor.submit(task, null, request))
                .doesNotThrowAnyException();

        verify(taskService, atLeastOnce()).update(any(Task.class));
    }

    @Test
    void testSubmit_shouldProcessMultipleTasksInOrder() throws Exception {
        Task task1 = Task.builder().id("123").personId(PERSON_ID).build();
        Task task2 = Task.builder().id("456").personId(PERSON_ID).build();

        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        taskProcessor.submit(task1, null, request);
        taskProcessor.submit(task2, null, request);

        Thread.sleep(500);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskService, atLeast(4)).update(captor.capture());

        List<Task> updates = captor.getAllValues();
        List<Task> doneTasks = updates.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE && t.getProgress() == 100)
                .toList();

        assertThat(doneTasks).extracting(Task::getId).contains("123", "456");
    }
}
