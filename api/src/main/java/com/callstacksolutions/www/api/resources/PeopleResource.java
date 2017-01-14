package com.callstacksolutions.www.api.resources;

import com.callstacksolutions.www.dataaccess.PersonDAO;
import com.callstacksolutions.www.domain.Person;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PeopleResource {

    private final PersonDAO personDAO;

    public PeopleResource(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @POST
    @UnitOfWork
    public Person createPerson(Person person) {
        return personDAO.create(person);
    }

    @GET
    @UnitOfWork
    public List<Person> listPeople() {
        return personDAO.findAll();
    }

}
