package com.callstacksolutions.www.application;

import com.callstacksolutions.www.application.configuration.SuggestionApiServiceConfiguration;
import com.callstacksolutions.www.dataaccess.person.PersonEntity;
import com.callstacksolutions.www.domain.Saying;
import com.google.common.base.Optional;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

    private static final String TMP_FILE = createTempFile();
    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test.yml");

    @ClassRule
    public static final DropwizardAppRule<SuggestionApiServiceConfiguration> RULE = new DropwizardAppRule<>(
            SuggestionApiService.class, CONFIG_PATH,
            ConfigOverride.config("database.url", "jdbc:h2:" + TMP_FILE));

    private Client client;

    @BeforeClass
    public static void migrateDb() throws Exception {
        RULE.getApplication().run("db", "migrate", CONFIG_PATH);
    }

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    private static String createTempFile() {
        try {
            return File.createTempFile("test-example", null).getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testHelloWorld() throws Exception {
        final Optional<String> name = Optional.fromNullable("Dr. IntegrationTest");
        final Saying saying = client.target("http://localhost:" + RULE.getLocalPort() + "/helloworld")
                .queryParam("name", name.get())
                .request()
                .get(Saying.class);
        assertThat(saying.getContent()).isEqualTo(RULE
                .getConfiguration()
                .getTemplateConfiguration()
                .buildTemplate()
                .render(name));
    }

    @Test
    public void testPostPerson() throws Exception {
        final PersonEntity personEntity = new PersonEntity("Dr. IntegrationTest", "Chief Wizard");
        final PersonEntity newPersonEntity = client.target("http://localhost:" + RULE.getLocalPort() + "/people")
                .request()
                .post(Entity.entity(personEntity, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(PersonEntity.class);
        assertThat(newPersonEntity.getId()).isNotNull();
        assertThat(newPersonEntity.getFullName()).isEqualTo(personEntity.getFullName());
        assertThat(newPersonEntity.getJobTitle()).isEqualTo(personEntity.getJobTitle());
    }


    @Test
    public void testListPeople() throws Exception {
        final List<PersonEntity> people = client.target("http://localhost:" + RULE.getLocalPort() + "/people")
                .request()
                .get()
                .readEntity(new GenericType<List<PersonEntity>>() {});

        for (PersonEntity personEntity : people) {
            assertThat(personEntity.getId()).isNotNull();
            assertThat(personEntity.getFullName()).isEqualTo(personEntity.getFullName());
            assertThat(personEntity.getJobTitle()).isEqualTo(personEntity.getJobTitle());
        }
    }
}
