package com.cdq.recruitmenttask.mapper;

import com.cdq.recruitmenttask.dto.PersonRequest;
import com.cdq.recruitmenttask.dto.PersonResponse;
import com.cdq.recruitmenttask.model.Person;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PersonMapper {

    Person toPersonEntity(PersonRequest request);

    PersonResponse toPersonResponse(Person person);
}