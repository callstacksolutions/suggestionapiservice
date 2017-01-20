package com.callstacksolutions.www.dataaccess.person;

import com.callstacksolutions.www.domain.person.IPersonDataAccessor;
import com.callstacksolutions.www.domain.person.Person;
import io.dropwizard.hibernate.HibernateBundle;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

public class PersonDataAccessor implements IPersonDataAccessor {

    @Inject
    private HibernateBundle hibernateBundle;

    private final PersonMapper personMapper = new PersonMapper();

    @Override
    public Person readPersonById(Long id) {
        PersonEntity personEntity = findPersonSafelyThroughDao(id);

        return personMapper.toDomain(personEntity);
    }

    @Override
    public List<Person> readListOfAllPeople() {
        return readListOfAllPeopleThroughDao();
    }

    @Override
    public Person createPerson(Person person) {
        return createPersonThroughDao(person);
    }


    private PersonEntity findPersonSafelyThroughDao(Long personId) {
        PersonDAO personDAO = new PersonDAO(hibernateBundle.getSessionFactory());

        final Optional<PersonEntity> personEntity = personDAO.findById(personId);
        if (!personEntity.isPresent()) {
            throw new NotFoundException("No such person.");
        }

        return personEntity.get();
    }

    private List<Person> readListOfAllPeopleThroughDao() {
        PersonDAO personDAO = new PersonDAO(hibernateBundle.getSessionFactory());

        List<PersonEntity> personEntities = personDAO.findAll();
        List<Person> people = personMapper.toDomain(personEntities);

        return people;
    }

    private Person createPersonThroughDao(Person person) {
        PersonDAO personDAO = new PersonDAO(hibernateBundle.getSessionFactory());

        PersonEntity personEntity = personMapper.fromDomain(person);
        PersonEntity createdPersonEntity = personDAO.create(personEntity);
        Person createdPerson = personMapper.toDomain(createdPersonEntity);

        return createdPerson;
    }
}
