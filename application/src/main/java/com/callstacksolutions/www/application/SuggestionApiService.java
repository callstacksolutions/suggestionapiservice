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
import com.callstacksolutions.www.crosscutting.health.TemplateHealthCheck;
import com.callstacksolutions.www.dataaccess.PersonDAO;
import com.callstacksolutions.www.domain.Person;
import com.callstacksolutions.www.domain.Template;
import com.callstacksolutions.www.domain.User;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
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
        return "helloworld";
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
    }

    @Override
    public void run(SuggestionApiServiceConfiguration configuration, Environment environment) {
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        final Template template = configuration.buildTemplate();

        environment.healthChecks().register("template", new TemplateHealthCheck(template));
        environment.jersey().register(DateRequiredFeature.class);
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new ExampleAuthenticator())
                .setAuthorizer(new ExampleAuthorizer())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new ViewResource());
        environment.jersey().register(new ProtectedResource());
        environment.jersey().register(new PeopleResource(dao));
        environment.jersey().register(new PersonResource(dao));
        environment.jersey().register(new FilteredResource());
    }
}
