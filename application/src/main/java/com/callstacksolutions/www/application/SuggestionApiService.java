package com.callstacksolutions.www.application;

import com.callstacksolutions.www.api.auth.ExampleAuthenticator;
import com.callstacksolutions.www.api.auth.ExampleAuthorizer;
import com.callstacksolutions.www.api.filters.DateRequiredFeature;
import com.callstacksolutions.www.api.resources.FilteredResource;
import com.callstacksolutions.www.api.resources.HelloWorldResource;
import com.callstacksolutions.www.api.resources.PeopleResource;
import com.callstacksolutions.www.api.resources.PersonResource;
import com.callstacksolutions.www.api.resources.ProtectedResource;
import com.callstacksolutions.www.api.resources.ViewResource;
import com.callstacksolutions.www.application.client.RenderCommand;
import com.callstacksolutions.www.crosscutting.dependencyinjection.ApiModule;
import com.callstacksolutions.www.crosscutting.dependencyinjection.CrossCuttingModule;
import com.callstacksolutions.www.crosscutting.dependencyinjection.DataAccessModule;
import com.callstacksolutions.www.crosscutting.dependencyinjection.DomainModule;
import com.callstacksolutions.www.crosscutting.health.TemplateHealthCheck;
import com.callstacksolutions.www.dataaccess.PersonDAO;
import com.callstacksolutions.www.domain.Person;
import com.callstacksolutions.www.domain.Template;
import com.callstacksolutions.www.domain.User;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.util.Map;

public class SuggestionApiService extends Application<SuggestionApiServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new SuggestionApiService().run(args);
    }

    private final HibernateBundle<SuggestionApiServiceConfiguration> hibernateBundle =
            new HibernateBundle<SuggestionApiServiceConfiguration>(Person.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(SuggestionApiServiceConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "Suggestion API Service";
    }

    @Override
    public void initialize(Bootstrap<SuggestionApiServiceConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<SuggestionApiServiceConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(SuggestionApiServiceConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<SuggestionApiServiceConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(SuggestionApiServiceConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });

        initializeDependencyInjection(bootstrap);
    }

    private void initializeDependencyInjection(Bootstrap<SuggestionApiServiceConfiguration> bootstrap) {
        GuiceBundle<SuggestionApiServiceConfiguration> guiceBundle =
                GuiceBundle.<SuggestionApiServiceConfiguration>newBuilder()
                        .addModule(new ApiModule())
                        .addModule(new CrossCuttingModule())
                        .addModule(new DataAccessModule())
                        .addModule(new DomainModule())
                        .setConfigClass(SuggestionApiServiceConfiguration.class)
                        .enableAutoConfig(getClass().getPackage().getName())
                        .build();

        bootstrap.addBundle(guiceBundle);
    }


    @Override
    public void run(SuggestionApiServiceConfiguration configuration, Environment environment) {
        registerFeatures(environment);
        registerAuthenticationAndAuthorization(environment);
        registerResources(configuration, environment);
    }

    private void registerFeatures(Environment environment) {
        environment.jersey().register(DateRequiredFeature.class);
    }

    private void registerAuthenticationAndAuthorization(Environment environment) {
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new ExampleAuthenticator())
                .setAuthorizer(new ExampleAuthorizer())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }

    private void registerResources(SuggestionApiServiceConfiguration configuration, Environment environment) {
        final Template template = configuration.buildTemplate();
        environment.healthChecks().register("template", new TemplateHealthCheck(template));
        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new ViewResource());
        environment.jersey().register(new ProtectedResource());

        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new PeopleResource(dao));
        environment.jersey().register(new PersonResource(dao));
        environment.jersey().register(new FilteredResource());
    }

}
