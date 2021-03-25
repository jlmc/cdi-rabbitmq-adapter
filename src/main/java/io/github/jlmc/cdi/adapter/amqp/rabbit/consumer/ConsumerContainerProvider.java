package io.github.jlmc.cdi.adapter.amqp.rabbit.consumer;

import com.rabbitmq.client.ConnectionFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Serializable;

@ApplicationScoped
public class ConsumerContainerProvider implements Serializable {

    @Inject
    ConnectionFactory connectionFactory;

    @Produces @Singleton
    public ConsumerContainer provideConsumerContainer() {
        return new ConsumerContainer(connectionFactory);
    }
}
