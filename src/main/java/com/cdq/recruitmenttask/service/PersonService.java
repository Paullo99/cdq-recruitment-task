package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.model.Person;

public interface PersonService {
    Person upsert(Person person);
}
