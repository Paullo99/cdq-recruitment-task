package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.async.TaskProcessor;
import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.TaskCreatedResponse;
import com.cdq.recruitmenttask.error.ApiException;
import com.cdq.recruitmenttask.model.Person;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskProcessor taskProcessor;

    @InjectMocks
    private PersonServiceImpl personService;

    private final PersonRequest personRequest = new PersonRequest("Anna", "Nowak", LocalDate.of(1999, 1, 1), "CDQ");
    private final String TASK_ID = "123";
    private final Long PERSON_ID = 1L;

    @Test
    void testCreateAndProcess_shouldCreatePersonAndTriggerTask() {
        Person saved = Person.builder().id(PERSON_ID).name("Anna").surname("Nowak").birthDate(LocalDate.of(1999, 1, 1)).company("CDQ").build();
        Task task = Task.builder().id(TASK_ID).personId(PERSON_ID).build();

        when(personRepository.save(any())).thenReturn(saved);
        when(taskService.createTask(PERSON_ID)).thenReturn(task);

        TaskCreatedResponse response = personService.createAndProcess(personRequest);

        assertThat(response.taskId()).isEqualTo(TASK_ID);
        verify(taskProcessor).submit(task, null, personRequest);
    }

    @Test
    void testUpdateAndProcess_shouldUpdatePersonAndTriggerTask() {
        Person existing = Person.builder().id(PERSON_ID).name("John").surname("Smith").birthDate(LocalDate.of(1990, 1, 1)).company("OldCo").build();
        Person updated = Person.builder().id(PERSON_ID).name("Anna").surname("Nowak").birthDate(LocalDate.of(1999, 1, 1)).company("CDQ").build();
        Task task = Task.builder().id(TASK_ID).personId(PERSON_ID).build();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.save(any())).thenReturn(updated);
        when(taskService.createTask(PERSON_ID)).thenReturn(task);

        TaskCreatedResponse response = personService.updateAndProcess(PERSON_ID, personRequest);

        assertThat(response.taskId()).isEqualTo(TASK_ID);
        verify(taskProcessor).submit(task, new PersonRequest("John", "Smith", LocalDate.of(1990, 1, 1), "OldCo"), personRequest);
    }

    @Test
    void testUpdateAndProcess_shouldThrow_whenUpdatingNonExistingPerson() {
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.updateAndProcess(PERSON_ID, personRequest))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Person with ID 1 not found.");
    }

    @Test
    void testFindAll_shouldReturnAllPersons() {
        Person person1 = Person.builder().id(1L).name("Anna").surname("Nowak").birthDate(LocalDate.of(1999, 1, 1)).company("CDQ").build();
        Person person2 = Person.builder().id(2L).name("John").surname("Smith").birthDate(LocalDate.of(1990, 1, 1)).company("OldCo").build();

        when(personRepository.findAll()).thenReturn(List.of(person1, person2));

        var response = personService.findAll();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).name()).isEqualTo("Anna");
        assertThat(response.get(1).name()).isEqualTo("John");
    }
}
