package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.TaskCreatedResponse;

public interface PersonService {
    TaskCreatedResponse createAndProcess(PersonRequest request);
    TaskCreatedResponse updateAndProcess(Long id, PersonRequest request);
}
