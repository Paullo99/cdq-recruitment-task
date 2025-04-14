package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.PersonResponse;
import com.cdq.recruitmenttask.dto.TaskCreatedResponse;

import java.util.List;

public interface PersonService {
    TaskCreatedResponse createAndProcess(PersonRequest request);

    TaskCreatedResponse updateAndProcess(Long id, PersonRequest request);

    List<PersonResponse> findAll();
}
