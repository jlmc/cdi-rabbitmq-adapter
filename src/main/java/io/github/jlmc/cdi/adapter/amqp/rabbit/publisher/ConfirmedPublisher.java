package io.github.jlmc.cdi.adapter.amqp.rabbit.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;


/**
 * <p>A confirmed publisher sends messages to a broker
 * and waits for a confirmation that the message was
 * received by the broker.</p>
 *
 * @author christian.bick
 * @author uwe.janner
 * @author soner.dastan
 *
 */
public class ConfirmedPublisher extends DiscretePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmedPublisher.class);

    public ConfirmedPublisher(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public void publish(Message message, DeliveryOptions deliveryOptions) throws IOException,TimeoutException {
        for (int attempt = 1; attempt <= DEFAULT_RETRY_ATTEMPTS; attempt++) {
            if (attempt > 1) {
                LOGGER.info("Attempt {} to send message", attempt);
            }

            try {
                Channel channel = provideChannel();

                message.publish(channel, deliveryOptions);

                LOGGER.info("Waiting for publisher ack");

                channel.waitForConfirmsOrDie();

                LOGGER.info("Received publisher ack");
                return;
            } catch (IOException e) {
                handleIoException(attempt, e);
            } catch (InterruptedException e) {
                LOGGER.warn("Publishing message interrupted while waiting for producer ack", e);
                return;
            }
        }
    }

    @Override
    public void publish(List<Message> messages, DeliveryOptions deliveryOptions) throws IOException, TimeoutException{
        for (Message message : messages) {
            publish(message, deliveryOptions);
        }
    }

    @Override
    protected Channel provideChannel() throws IOException {
        Channel channel = super.provideChannel();
        channel.confirmSelect();
        return channel;
    }
}
