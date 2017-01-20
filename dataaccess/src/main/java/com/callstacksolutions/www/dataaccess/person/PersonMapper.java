package com.callstacksolutions.www.dataaccess.person;

import com.callstacksolutions.www.domain.person.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonMapper {

    public Person toDomain(PersonEntity personEntity) {
        Person person = new Person(personEntity.getId());
        person.setFullName(personEntity.getFullName());
        person.setJobTitle(personEntity.getJobTitle());

        return person;
    }

    public List<Person> toDomain(final List<PersonEntity> personEntities) {
        List<Person> people = new ArrayList<>();

        for (PersonEntity personEntity : personEntities) {
            people.add(toDomain(personEntity));
        }

        return people;
    }

    public PersonEntity fromDomain(Person person) {
        PersonEntity personEntity = new PersonEntity(person.getFullName(), person.getJobTitle());

        return personEntity;
    }

}
