package com.callstacksolutions.www.application;

import com.callstacksolutions.www.api.auth.ExampleAuthenticator;
import com.callstacksolutions.www.api.auth.ExampleAuthorizer;
import com.callstacksolutions.www.api.filters.DateRequiredFeature;
import com.callstacksolutions.www.api.person.PersonResource;
import com.callstacksolutions.www.api.resources.FilteredResource;
import com.callstacksolutions.www.api.resources.HelloWorldResource;
import com.callstacksolutions.www.api.resources.ProtectedResource;
import com.callstacksolutions.www.api.resources.ViewResource;
import com.callstacksolutions.www.application.client.RenderCommand;
import com.callstacksolutions.www.application.configuration.SuggestionApiServiceConfiguration;
import com.callstacksolutions.www.application.dependencyinjection.ApiModule;
import com.callstacksolutions.www.application.dependencyinjection.ApplicationModule;
import com.callstacksolutions.www.application.dependencyinjection.CrossCuttingModule;
import com.callstacksolutions.www.application.dependencyinjection.DataAccessModule;
import com.callstacksolutions.www.application.dependencyinjection.DomainModule;
import com.callstacksolutions.www.crosscutting.health.TemplateHealthCheck;
import com.callstacksolutions.www.domain.Template;
import com.callstacksolutions.www.domain.User;
import com.google.inject.Stage;
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

    private GuiceBundle guiceBundle;
    private HibernateBundle hibernateBundle;

    public static void main(String[] args) throws Exception {
        new SuggestionApiService().run(args);
    }


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
                return configuration.getDatabase();
            }
        });
        bootstrap.addBundle(new ViewBundle<SuggestionApiServiceConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(SuggestionApiServiceConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });

        initializeGuiceBundle(bootstrap);
        initializeHibernateBundle(bootstrap);
    }

    private void initializeGuiceBundle(Bootstrap<SuggestionApiServiceConfiguration> bootstrap) {
        guiceBundle = GuiceBundle.<SuggestionApiServiceConfiguration>newBuilder()
                .addModule(new ApiModule())
                .addModule(new ApplicationModule())
                .addModule(new CrossCuttingModule())
                .addModule(new DataAccessModule())
                .addModule(new DomainModule())
                .setConfigClass(SuggestionApiServiceConfiguration.class)
                .enableAutoConfig(getClass().getPackage().getName())
                .build(Stage.DEVELOPMENT); // Workaround for a bug when using @Singleton

        bootstrap.addBundle(guiceBundle);
    }

    private void initializeHibernateBundle(Bootstrap<SuggestionApiServiceConfiguration> bootstrap) {
        hibernateBundle = guiceBundle.getInjector().getProvider(HibernateBundle.class).get();

        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(SuggestionApiServiceConfiguration configuration, Environment environment) {
        registerFeatures(environment);
        registerAuthenticationAndAuthorization(environment);
        registerHealthChecks(configuration, environment);
        registerResources(configuration, environment);
    }

    private void registerFeatures(Environment environment) {
        environment.jersey().register(DateRequiredFeature.class);
    }

    private void registerAuthenticationAndAuthorization(Environment environment) {
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new ExampleAuthenticator())
                        .setAuthorizer(new ExampleAuthorizer())
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }

    private void registerHealthChecks(SuggestionApiServiceConfiguration configuration, Environment environment) {
        final Template template = configuration.getTemplateConfiguration().buildTemplate();
        environment.healthChecks().register("template", new TemplateHealthCheck(template));
    }

    private void registerResources(SuggestionApiServiceConfiguration configuration, Environment environment) {
        environment.jersey().register(guiceBundle.getInjector().getInstance(HelloWorldResource.class));
        environment.jersey().register(guiceBundle.getInjector().getInstance(ViewResource.class));
        environment.jersey().register(guiceBundle.getInjector().getInstance(ProtectedResource.class));
        environment.jersey().register(guiceBundle.getInjector().getInstance(FilteredResource.class));
        environment.jersey().register(guiceBundle.getInjector().getInstance(PersonResource.class));
    }

}
