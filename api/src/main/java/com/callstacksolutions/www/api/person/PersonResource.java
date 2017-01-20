package com.callstacksolutions.www.api.person;

import com.callstacksolutions.www.domain.person.IPersonService;
import com.callstacksolutions.www.domain.person.Person;
import com.callstacksolutions.www.web.views.PersonView;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(PersonResource.PERSON_ENDPOINT)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    public static final String PERSON_ENDPOINT = "/people";

    private final PersonMapper personMapper;
    private final IPersonService personService;

    @Inject
    public PersonResource(PersonMapper personMapper, IPersonService personService) {
        this.personMapper = personMapper;
        this.personService = personService;
    }

    @GET
    @Path("{personId}")
    @UnitOfWork
    public PersonDto getPerson(@PathParam("personId") LongParam personId) {
        PersonDto personDto = personMapper.fromDomain(getPersonFromPersonService(personId));

        return personDto;
    }

    @GET
    @Path("{personId}/view_freemarker")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public PersonView getPersonViewFreemarker(@PathParam("personId") LongParam personId) {
        PersonView personView = new PersonView(PersonView.Template.FREEMARKER, getPersonFromPersonService(personId));

        return personView;
    }

    @GET
    @Path("{personId}/view_mustache")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public PersonView getPersonViewMustache(@PathParam("personId") LongParam personId) {
        return new PersonView(PersonView.Template.MUSTACHE, getPersonFromPersonService(personId));
    }

    @GET
    @UnitOfWork
    public List<PersonDto> getListOfAllPeople() {
        return getListOfAllPeopleFromPersonService();
    }

    @POST
    @UnitOfWork
    public PersonDto postPerson(PersonDto personDto) {
        return createPersonThroughPersonService(personDto);
    }

    private Person getPersonFromPersonService(LongParam personId) {
        Long id = personId.get();
        Person person = personService.getPersonById(id);

        return person;
    }

    private List<PersonDto> getListOfAllPeopleFromPersonService() {
        List<Person> people = personService.getListOfAllPeople();
        List<PersonDto> personDtos = personMapper.fromDomain(people);

        return personDtos;
    }

    private PersonDto createPersonThroughPersonService(PersonDto personDto) {
        Person person = personMapper.toDomain(personDto);
        Person createdPerson = personService.createPerson(person);
        PersonDto createdPersonDto = personMapper.fromDomain(createdPerson);

        return createdPersonDto;
    }
}
