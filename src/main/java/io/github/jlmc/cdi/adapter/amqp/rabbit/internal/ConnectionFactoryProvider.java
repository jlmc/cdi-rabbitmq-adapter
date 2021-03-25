package io.github.jlmc.cdi.adapter.amqp.rabbit.internal;

import com.rabbitmq.client.ConnectionFactory;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionFactoryConfigurations;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionFactoryConfigurationsConfigurator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Serializable;

@ApplicationScoped
public class ConnectionFactoryProvider implements Serializable {

    @Inject
    Instance<ConnectionFactoryConfigurationsConfigurator> connectionFactoryConfigurators;

    @Produces
    @Singleton
    public ConnectionFactory provideConnectionFactory() {
        if (connectionFactoryConfigurators.isUnsatisfied()) {

             ConnectionFactoryConfigurations configurations
                    = ConnectionFactoryConfigurations.defaults()
                                                 .setUsername("demo")
                                                 .setPassword("demo");
            return createConnectionFactory(configurations);
        }

        ConnectionFactoryConfigurationsConfigurator connectionFactoryConfigurationsConfigurator = connectionFactoryConfigurators.get();
        ConnectionFactoryConfigurations configurations = connectionFactoryConfigurationsConfigurator.configure();
        return createConnectionFactory(configurations);
    }

    private ConnectionFactory createConnectionFactory(ConnectionFactoryConfigurations configurations) {
        return new SingleConnectionFactory(configurations);
    }

    public void closeConnectionFactory(@Disposes ConnectionFactory connectionFactory) {
        if (connectionFactory instanceof SingleConnectionFactory) {
            ((SingleConnectionFactory)connectionFactory).close();
        }
    }

}
