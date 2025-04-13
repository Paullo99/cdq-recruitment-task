package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.async.TaskProcessor;
import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.TaskCreatedResponse;
import com.cdq.recruitmenttask.model.Person;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final TaskService taskService;
    private final TaskProcessor taskProcessor;

    @Override
    public TaskCreatedResponse createAndProcess(PersonRequest request) {
        Person person = mapToEntity(request);
        Person saved = personRepository.save(person);

        Task task = taskService.createTask(saved.getId());
        taskProcessor.submit(task, null, request);

        return new TaskCreatedResponse(task.getId());
    }

    @Override
    public TaskCreatedResponse updateAndProcess(Long id, PersonRequest request) {
        PersonRequest oldData = findById(id)
                .map(p -> new PersonRequest(p.getName(), p.getSurname(), p.getBirthDate(), p.getCompany()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Person updated = mapToEntity(request);
        updated.setId(id);
        Person saved = personRepository.save(updated);

        Task task = taskService.createTask(saved.getId());
        taskProcessor.submit(task, oldData, request);

        return new TaskCreatedResponse(task.getId());
    }

    @Override
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    private Person mapToEntity(PersonRequest req) {
        return Person.builder()
                .name(req.name())
                .surname(req.surname())
                .birthDate(req.birthDate())
                .company(req.company())
                .build();
    }
}