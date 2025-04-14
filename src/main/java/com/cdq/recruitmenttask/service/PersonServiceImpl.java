package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.async.TaskProcessor;
import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.PersonResponse;
import com.cdq.recruitmenttask.dto.TaskCreatedResponse;
import com.cdq.recruitmenttask.error.ApiException;
import com.cdq.recruitmenttask.error.ErrorCode;
import com.cdq.recruitmenttask.mapper.PersonMapper;
import com.cdq.recruitmenttask.model.Person;
import com.cdq.recruitmenttask.model.Task;
import com.cdq.recruitmenttask.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final TaskService taskService;
    private final TaskProcessor taskProcessor;
    private final PersonMapper personMapper;

    @Override
    public TaskCreatedResponse createAndProcess(PersonRequest request) {
        Person person = personMapper.toPersonEntity(request);
        Person saved = personRepository.save(person);

        Task task = taskService.createTask(saved.getId());
        taskProcessor.submit(task, null, request);

        return new TaskCreatedResponse(task.getId());
    }

    @Override
    public TaskCreatedResponse updateAndProcess(Long id, PersonRequest request) {
        PersonRequest oldData = personRepository.findById(id)
                .map(p -> new PersonRequest(p.getName(), p.getSurname(), p.getBirthDate(), p.getCompany()))
                .orElseThrow(() -> new ApiException(
                        ErrorCode.PERSON_NOT_FOUND,
                        "Person with ID " + id + " not found."
                ));

        Person updated = personMapper.toPersonEntity(request);
        updated.setId(id);
        Person saved = personRepository.save(updated);

        Task task = taskService.createTask(saved.getId());
        taskProcessor.submit(task, oldData, request);

        return new TaskCreatedResponse(task.getId());
    }

    @Override
    public List<PersonResponse> findAll() {
        return personRepository.findAll()
                .stream()
                .map(personMapper::toPersonResponse)
                .toList();
    }
}