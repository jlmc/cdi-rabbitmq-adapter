package io.github.jlmc.cdi.adapter.amqp.rabbit.publisher;

import com.rabbitmq.client.ConnectionFactory;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventWriter;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Message;
import io.github.jlmc.cdi.adapter.amqp.rabbit.events.EventWriterHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Publishes events to exchanges of a broker.
 */
@ApplicationScoped
public class EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublisher.class);

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    EventWriterHolder eventWriterHolder;

    private Map<Class<?>, EventPublisherEntryConfiguration> publisherConfigurations = new HashMap<>();

    ThreadLocal<Map<Class<?>, MessagePublisher>> publishers = new ThreadLocal<>();

    /**
     * Adds events of the given type to the CDI events to which the event publisher listens in order to
     * publish them. The publisher configuration is used to decide where to and how to publish messages.
     * @param eventType The event type
     * @param configuration The configuration used when publishing and event
     */
    public <T> void addEvent(Class<T> eventType, EventPublisherEntryConfiguration configuration) {
        publisherConfigurations.put(eventType, configuration);
    }

    /**
     * Observes CDI events for remote events and publishes those events if their event type
     * was added before.
     *
     * @param event The event to publish
     */
    public void publishEvent(@Observes Object event) throws IOException, TimeoutException {
        Class<?> eventType = event.getClass();

        LOGGER.debug("Receiving event of type <{}>", eventType.getSimpleName());

        if (!publisherConfigurations.containsKey(eventType)) {
            LOGGER.debug("No publisher configured for event of type <{}>", eventType.getSimpleName());
            return;
        }

        EventPublisherEntryConfiguration configuration = publisherConfigurations.get(eventType);

        MessagePublisher publisher = providePublisher(configuration, eventType);

        Message message = buildMessage(configuration, event);

        try {

            LOGGER.info("Publishing event of type {}", eventType.getSimpleName());
            publisher.publish(message, configuration.getDeliveryOptions());
            LOGGER.info("Successfully published event of type {}", eventType.getSimpleName());

        } catch (IOException | TimeoutException e) {
            LOGGER.error("Failed to publish event {}", eventType.getSimpleName(), e);
            throw e;
        }
    }

    private MessagePublisher providePublisher(EventPublisherEntryConfiguration configuration, Class<?> eventType) {
        Map<Class<?>, MessagePublisher> localPublishers = publishers.get();

        if (localPublishers == null) {
            localPublishers = new HashMap<>();
            publishers.set(localPublishers);
        }

        MessagePublisher publisher = localPublishers.get(eventType);

        if (publisher == null) {

            publisher = new GenericPublisher(connectionFactory, configuration.getReliability());

            localPublishers.put(eventType, publisher);
        }

        return publisher;
    }

    private Message buildMessage(EventPublisherEntryConfiguration configuration, Object event) {
        Message message =
                new Message(configuration.getBasicProperties())
                        .exchange(configuration.getExchange())
                        .routingKey(configuration.getRoutingKey())
                        .contentType(ContentTypes.APPLICATION_JSON)
                        .contentEncoding(StandardCharsets.UTF_8.name());


        if (configuration.isPersistent()) {
            message.persistent();
        }

        EventWriter writer = eventWriterHolder.findWriter(message.getContentType());

        byte[] bodyContent = writer.write(event, event.getClass());

        message.body(bodyContent);

        return message;
    }

    
}
