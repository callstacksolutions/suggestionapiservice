package com.callstacksolutions.www.domain.person;

import javax.inject.Inject;
import java.util.List;

public class PersonService implements IPersonService {

    @Inject
    private IPersonDataAccessor personDataAccessor;

    @Override
    public List<Person> getListOfAllPeople() {
        return personDataAccessor.readListOfAllPeople();
    }

    @Override
    public Person getPersonById(Long id) {
        return personDataAccessor.readPersonById(id);
    }

    @Override
    public Person createPerson(Person person) {
        return personDataAccessor.createPerson(person);
    }
}

