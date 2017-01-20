package com.callstacksolutions.www.application.dependencyinjection;

import com.callstacksolutions.www.dataaccess.person.PersonDataAccessor;
import com.callstacksolutions.www.domain.person.IPersonDataAccessor;
import com.google.inject.AbstractModule;

public class DataAccessModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IPersonDataAccessor.class).to(PersonDataAccessor.class);
    }
}
