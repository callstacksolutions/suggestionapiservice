package com.callstacksolutions.www.api.person;

import com.callstacksolutions.www.domain.person.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonMapper {

    public Person toDomain(PersonDto personDto) {
        Person person = new Person(personDto.getId());
        person.setFullName(personDto.getFullName());
        person.setJobTitle(personDto.getJobTitle());

        return person;
    }
    
    public List<Person> toDomain(List<PersonDto> personDtos) {
        List<Person> people = new ArrayList<>();

        for (PersonDto personDto: personDtos) {
            people.add(toDomain(personDto));
        }

        return people;
    }

    public PersonDto fromDomain(Person person) {
        PersonDto personDto = new PersonDto(person.getId());

        personDto.setFullName(person.getFullName());
        personDto.setJobTitle(person.getJobTitle());

        return personDto;
    }

    public List<PersonDto> fromDomain(final List<Person> people) {
        List<PersonDto> personDtos = new ArrayList<>();

        for (Person person: people) {
            personDtos.add(fromDomain(person));
        }

        return personDtos;
    }

}
