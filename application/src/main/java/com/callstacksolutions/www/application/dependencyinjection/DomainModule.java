package com.callstacksolutions.www.application.dependencyinjection;

import com.callstacksolutions.www.domain.person.IPersonService;
import com.callstacksolutions.www.domain.person.PersonService;
import com.google.inject.AbstractModule;

public class DomainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IPersonService.class).to(PersonService.class);
    }
}
