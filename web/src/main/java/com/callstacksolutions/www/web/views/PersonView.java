package com.callstacksolutions.www.web.views;

import com.callstacksolutions.www.domain.person.Person;
import io.dropwizard.views.View;

public class PersonView extends View {

    // The path of the template is relative to package name of this class.
    // Therefore, freemarker/person.ftl will be found in
    // resources/com/callstacksolutions/www/web/views/freemarker/person.ftl;
    // The same applies for the mustache file.
    private static String PERSON_FTL_RESOURCE = "freemarker/person.ftl";
    private static String PERSON_MUSTACHE_RESOURCE = "mustache/person.mustache";

    private final Person person;

    // TODO: Can we use domain.Template here?
    public enum Template {
        FREEMARKER(PERSON_FTL_RESOURCE),
        MUSTACHE(PERSON_MUSTACHE_RESOURCE);

        private String templateName;

        Template(String templateName) {
            this.templateName = templateName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }

    public PersonView(PersonView.Template template, Person person) {
        super(template.getTemplateName());
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}
