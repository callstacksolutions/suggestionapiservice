package com.callstacksolutions.www.domain.person;

import java.util.List;

public interface IPersonService {
    Person getPersonById(Long id);
    List<Person> getListOfAllPeople();
    Person createPerson(Person person);
}
