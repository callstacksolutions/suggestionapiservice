package com.callstacksolutions.www.web.views;

import com.callstacksolutions.www.domain.Person;

import io.dropwizard.views.View;

public class PersonView extends View {
    private final Person person;

    public enum Template {
        // The path of the template is relative to package name of this class.
        // Therefore, freemarker/person.ftl will be found in
        // resources/com/callstacksolutions/www/web/views/freemarker/person.ftl;
        FREEMARKER("freemarker/person.ftl"),
        MUSTACHE("mustache/person.mustache");

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
