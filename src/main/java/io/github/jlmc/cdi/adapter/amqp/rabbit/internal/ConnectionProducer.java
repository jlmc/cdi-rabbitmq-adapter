package io.github.jlmc.cdi.adapter.amqp.rabbit.internal;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class ConnectionProducer implements Serializable {

    @Inject
    ConnectionFactory connectionFactory;

    @Produces
    @Singleton
    public Connection expose() {
        try {
            return connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new IllegalStateException(e);
        }
    }
}
