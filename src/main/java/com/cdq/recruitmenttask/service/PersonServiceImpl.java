package com.cdq.recruitmenttask.service;

import com.cdq.recruitmenttask.model.Person;
import com.cdq.recruitmenttask.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public Person upsert(Person person) {
        return personRepository.save(person);
    }
}