package com.callstacksolutions.www.application.dependencyinjection;

import com.callstacksolutions.www.application.configuration.SuggestionApiServiceConfiguration;
import com.callstacksolutions.www.application.configuration.TemplateConfiguration;
import com.callstacksolutions.www.domain.Person;
import com.callstacksolutions.www.domain.Template;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;

import javax.inject.Singleton;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    public TemplateConfiguration providesTemplateConfiguration(
            SuggestionApiServiceConfiguration suggestionApiServiceConfiguration) {

        return suggestionApiServiceConfiguration.getTemplateConfiguration();
    }

    @Provides
    private Template providesTemplate(TemplateConfiguration templateConfiguration) {

        return templateConfiguration.buildTemplate();
    }

    @Provides
    @Singleton
    private HibernateBundle providesHibernateBundle() {
        HibernateBundle hibernateBundle =
                new HibernateBundle<SuggestionApiServiceConfiguration>(Person.class) {

            @Override
            public DataSourceFactory getDataSourceFactory(SuggestionApiServiceConfiguration configuration) {
                return configuration.getDatabase();
            }
        };

        return hibernateBundle;
    }

}
