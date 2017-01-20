package com.callstacksolutions.www.api.resources;

import com.callstacksolutions.www.api.person.PersonDto;
import com.callstacksolutions.www.api.person.PersonMapper;
import com.callstacksolutions.www.api.person.PersonResource;
import com.callstacksolutions.www.domain.person.IPersonService;
import com.callstacksolutions.www.domain.person.Person;
import com.callstacksolutions.www.domain.person.PersonService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PersonResource}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonResourceTest {
    private static final IPersonService MOCK_PERSON_SERVICE = mock(PersonService.class);
    private static final PersonMapper MOCK_PERSON_MAPPER = mock(PersonMapper.class);
    private static final PersonResource PERSON_RESOURCE_UNDER_TEST =
            new PersonResource(MOCK_PERSON_MAPPER, MOCK_PERSON_SERVICE);

    private PersonDto personDto;
    private Person personMappedFromDto;
    private final Long id = new Long(0);
    private final String fullName = "Jane Doe";
    private final String jobTitle = "Senior Software Developer";

    @ClassRule
    public static final ResourceTestRule RESOURCE_TEST_RULE = ResourceTestRule.builder()
            .addResource(PERSON_RESOURCE_UNDER_TEST)
            //.setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();

    @Captor
    private ArgumentCaptor<Person> personCaptor;

    @Before
    public void setUp() {
        personDto = new PersonDto(id);
        personDto.setFullName(fullName);
        personDto.setJobTitle(jobTitle);

        personMappedFromDto = new Person(id);
        personMappedFromDto.setFullName(fullName);
        personMappedFromDto.setJobTitle(jobTitle);
    }

    @After
    public void tearDown() {
        reset(MOCK_PERSON_SERVICE);
        reset(MOCK_PERSON_MAPPER);
        reset(MOCK_PERSON_SERVICE);
    }

    @Test
    public void getPerson_withExistingId_returnsPerson() {
        when(MOCK_PERSON_MAPPER.toDomain(any(PersonDto.class))).thenReturn(personMappedFromDto);
        when(MOCK_PERSON_MAPPER.fromDomain(any(Person.class))).thenReturn(personDto);
        when(MOCK_PERSON_SERVICE.getPersonById(personMappedFromDto.getId())).thenReturn(personMappedFromDto);

        String targetPath = PersonResource.PERSON_ENDPOINT + "/" + personMappedFromDto.getId();

        PersonDto retrievedPersonDto = RESOURCE_TEST_RULE
                .getJerseyTest()
                .target(targetPath)
                .request().get(PersonDto.class);

        assertThat(retrievedPersonDto.getId()).isEqualTo(personDto.getId());
        verify(MOCK_PERSON_SERVICE).getPersonById(personMappedFromDto.getId());
    }

    @Test
    public void getPerson_withoutExistingId_returnsNotFound() {
        Long invalidId = new Long(2);

        when(MOCK_PERSON_SERVICE.getPersonById(invalidId)).thenThrow(new NotFoundException());

        String targetPath = PersonResource.PERSON_ENDPOINT + "/" + invalidId;

        final Response response = RESOURCE_TEST_RULE
                .getJerseyTest()
                .target(targetPath)
                .request()
                .get();

        assertThat(response.getStatusInfo().getStatusCode())
                .isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        verify(MOCK_PERSON_SERVICE).getPersonById(invalidId);
    }

    @Test
    public void getListOfAllPeople_returnsListAllPeople() {
        final List<Person> people = new ArrayList<>();
        people.add(personMappedFromDto);

        final List<PersonDto> personDtos = new ArrayList<>();
        personDtos.add(personDto);

        when(MOCK_PERSON_MAPPER.fromDomain(anyListOf(Person.class))).thenReturn(personDtos);
        when(MOCK_PERSON_MAPPER.fromDomain(any(Person.class))).thenReturn(personDto);
        when(MOCK_PERSON_SERVICE.getListOfAllPeople()).thenReturn(people);

        final List<PersonDto> responsePersonDtos = RESOURCE_TEST_RULE
                .client()
                .target(PersonResource.PERSON_ENDPOINT)
                .request()
                .get(new GenericType<List<PersonDto>>(){});

        verify(MOCK_PERSON_SERVICE).getListOfAllPeople();
        assertThat(responsePersonDtos.contains(personDto));
    }

    @Test
    public void postPerson_withIdSpecified_returnsPersonWithGeneratedId() {
        Long generatedId = new Long(1);

        Person createdPerson = new Person(generatedId);
        createdPerson.setFullName(fullName);
        createdPerson.setJobTitle(jobTitle);

        PersonDto createdPersonDto = new PersonDto(generatedId);
        createdPersonDto.setFullName(fullName);
        createdPersonDto.setJobTitle(jobTitle);

        when(MOCK_PERSON_MAPPER.toDomain(any(PersonDto.class))).thenReturn(personMappedFromDto);
        when(MOCK_PERSON_MAPPER.fromDomain(any(Person.class))).thenReturn(createdPersonDto);
        when(MOCK_PERSON_SERVICE.createPerson(any(Person.class))).thenReturn(createdPerson);

        final Response response = RESOURCE_TEST_RULE
                .client()
                .target(PersonResource.PERSON_ENDPOINT)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(personDto, MediaType.APPLICATION_JSON_TYPE));

        final PersonDto responsePersonDto = response.readEntity(PersonDto.class);

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        verify(MOCK_PERSON_SERVICE).createPerson(personCaptor.capture());
        assertThat(personCaptor.getValue()).isEqualTo(personMappedFromDto);
        assertThat(responsePersonDto.getId()).isEqualTo(generatedId);
    }
}
