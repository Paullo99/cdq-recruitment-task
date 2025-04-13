package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.TaskCreatedResponse;
import com.cdq.recruitmenttask.model.Person;

import java.util.Optional;

public interface PersonService {
    TaskCreatedResponse createAndProcess(PersonRequest request);
    TaskCreatedResponse updateAndProcess(Long id, PersonRequest request);
    Optional<Person> findById(Long id);
}
