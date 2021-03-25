package io.github.jlmc.cdi.adapter.amqp.rabbit.publisher;

import com.rabbitmq.client.ConnectionFactory;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Message;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 *  * Publishes messages with certain different levels of {@link PublisherReliability}.
 */
public class GenericPublisher implements MessagePublisher {

    MessagePublisher publisher;

    /**
     * <p>Initializes the publisher with a certain level of reliability. All messages
     * sent by the producer are sent with this level of reliability. Uses the given
     * connection factory to establish connections.</p>
     *
     * @see SimplePublisher
     * @see ConfirmedPublisher
     * @see TransactionalPublisher
     *
     * @param connectionFactory The connection factory
     * @param reliability The reliability level
     */
    public GenericPublisher(ConnectionFactory connectionFactory, PublisherReliability reliability) {
        if (PublisherReliability.CONFIRMED == reliability) {
            publisher = new ConfirmedPublisher(connectionFactory);
        } else if (PublisherReliability.TRANSACTIONAL == reliability) {
            publisher = new TransactionalPublisher(connectionFactory);
        } else {
            publisher = new SimplePublisher(connectionFactory);
        }
    }

    @Override
    public void publish(Message message) throws IOException, TimeoutException {
        publish(message, DeliveryOptions.NONE);
    }

    @Override
    public void publish(List<Message> messages) throws IOException, TimeoutException {
        publish(messages, DeliveryOptions.NONE);
    }

    @Override
    public void publish(Message message, DeliveryOptions deliveryOptions) throws IOException, TimeoutException {
        publisher.publish(message, deliveryOptions);
    }


    @Override
    public void publish(List<Message> messages, DeliveryOptions deliveryOptions) throws IOException, TimeoutException {
        publisher.publish(messages, deliveryOptions);
    }

    @Override
    public void close() throws IOException, TimeoutException {
        publisher.close();
    }
}
