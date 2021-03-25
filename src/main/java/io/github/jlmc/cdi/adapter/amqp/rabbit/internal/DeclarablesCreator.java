package io.github.jlmc.cdi.adapter.amqp.rabbit.internal;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Binding;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Declarables;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Exchange;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Dependent
public class DeclarablesCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeclarablesCreator.class);

    @Inject
    Connection connection;

    public void create(Declarables declarables) {
        LOGGER.info("Declaring all the rabbit MQ resources");

        if (declarables == null || declarables.isEmpty()) {
            LOGGER.info("No Declarations defined to create!!");
            return;
        }

        try (Channel channel = createAMPQChanel()) {

            List<Exchange> exchanges = declarables.getDeclarablesByType(Exchange.class);
            if (!exchanges.isEmpty()) {
                declareExchanges(channel, exchanges);
            }

            List<Queue> queues = declarables.getDeclarablesByType(Queue.class);
            if (!queues.isEmpty()) {
                declareQueues(channel, queues);
            }

            List<Binding> bindings = declarables.getDeclarablesByType(Binding.class);
            if (!bindings.isEmpty()) {
                declareBindings(channel, bindings);
            }

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void declareBindings(Channel channel, List<Binding> bindings) {
        bindings.forEach(binding -> declareBinding(channel, binding));
    }

    private void declareBinding(Channel channel, Binding binding) {
        try {
            LOGGER.info("Declaring the Binding: <{}>", binding);

            Binding.DestinationType destinationType = binding.getDestinationType();

            switch (destinationType) {
                case QUEUE:

                    channel.queueBind(binding.getDestination(),
                                      binding.getExchange(),
                                      binding.getRoutingKey(),
                                      binding.getArguments());

                    break;
                case EXCHANGE:
                    channel.exchangeBind(binding.getDestination(),
                                         binding.getExchange(),
                                         binding.getRoutingKey(),
                                         binding.getArguments());
                    break;
                default:

            }

        } catch (Exception e) {
            throw handledException(e);
        }
    }

    private void declareQueues(Channel channel, List<Queue> queues) {
        queues.forEach(queue -> declareQueue(channel, queue));
    }

    private void declareQueue(Channel channel, Queue queue) {
        try {
            LOGGER.info("Declaring the Queue: <{}>", queue);

            //@formatter:off
            channel.queueDeclare(queue.getName(),
                                 queue.isDurable(),
                                 queue.isExclusive(),
                                 queue.isAutoDelete(),
                                 queue.getArguments());
            //@formatter:on

        } catch (IOException e) {
            throw handledException(e);
        }
    }

    private void declareExchanges(Channel channel, List<Exchange> exchanges) {
        exchanges.forEach(exchange -> declareExchange(channel, exchange));
    }

    private void declareExchange(Channel channel, Exchange exchange) {
        try {
            LOGGER.info("Declaring the Exchange: <{}>", exchange);

            //@formatter:off
            channel.exchangeDeclare(exchange.getName(),
                                    exchange.getType(),
                                    exchange.isDurable(),
                                    exchange.isAutoDelete(),
                                    exchange.isInternal(),
                                    exchange.getArguments());
            //@formatter:on

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Channel createAMPQChanel() {
        try {
            return connection.createChannel();
        } catch (IOException e) {
            throw handledException(e);
        }
    }

    private RuntimeException handledException(Exception cause) {
        return new IllegalStateException(cause);
    }
}
