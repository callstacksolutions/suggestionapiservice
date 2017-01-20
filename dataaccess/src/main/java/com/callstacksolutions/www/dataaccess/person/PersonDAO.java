package com.callstacksolutions.www.dataaccess.person;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class PersonDAO extends AbstractDAO<PersonEntity> {
    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<PersonEntity> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public PersonEntity create(PersonEntity personEntity) {
        return persist(personEntity);
    }

    public List<PersonEntity> findAll() {
        return list(namedQuery("PersonEntity.findAll"));
    }
}
