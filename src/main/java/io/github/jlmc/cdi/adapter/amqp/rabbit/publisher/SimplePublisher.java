package io.github.jlmc.cdi.adapter.amqp.rabbit.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class SimplePublisher extends DiscretePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePublisher.class);

    public SimplePublisher(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public void publish(Message message, DeliveryOptions deliveryOptions) throws IOException, TimeoutException {
        for (int attempt = 1; attempt <= DEFAULT_RETRY_ATTEMPTS; attempt++) {
            if (attempt > 1) {
                LOGGER.info("Attempt {} to send message", attempt);
            }

            Channel channel = provideChannel();
            message.publish(channel, deliveryOptions);
            return;

        }
    }

    @Override
    public void publish(List<Message> messages, DeliveryOptions deliveryOptions) throws IOException, TimeoutException {
        for (Message message : messages) {
            publish(message, deliveryOptions);
        }
    }
}
