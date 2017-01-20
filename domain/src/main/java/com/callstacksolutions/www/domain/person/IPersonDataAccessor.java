package com.callstacksolutions.www.domain.person;

import java.util.List;

public interface IPersonDataAccessor {
    Person readPersonById(Long id);
    List<Person> readListOfAllPeople();
    Person createPerson(Person person);
}
