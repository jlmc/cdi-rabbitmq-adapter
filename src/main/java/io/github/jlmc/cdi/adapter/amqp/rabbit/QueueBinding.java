package io.github.jlmc.cdi.adapter.amqp.rabbit;

import io.github.jlmc.cdi.adapter.amqp.rabbit.consumer.ConsumerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures and stores the binding between and event class and a queue.
 */
public final class QueueBinding {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueBinding.class);

    private final Class<?> eventType;
    private final String queue;
    private boolean autoAck = false;
    private int consumerInstances = ConsumerConfiguration.DEFAULT_AMOUNT_OF_INSTANCES;
    private int prefetchMessageCount = ConsumerConfiguration.UNLIMITED_PREFETCH_MESSAGE_COUNT;

    QueueBinding(Class<?> eventType, String queue) {
        this.eventType = eventType;
        this.queue = queue;
        LOGGER.info("Binding created between queue <{}> and event type <{}>", queue, eventType.getSimpleName());
    }

    /**
     * <p>Sets the acknowledgement mode to be used for consuming message to automatic acknowledges
     * (auto acks).</p>
     *
     * <p>If auto acks is enabled, messages are delivered by the broker to its consumers in
     * a fire-and-forget manner. The broker removes a message from the queue as soon as its
     * is delivered to the consumer and does not care about whether the consumer successfully
     * processes this message or not.</p>
     */
    public QueueBinding autoAck() {
        this.autoAck = true;
        LOGGER.info("Auto acknowledges enabled for event type <{}>", eventType.getSimpleName());
        return this;
    }

    /**
     * Set the consumer instance to the given queue.
     */
    public QueueBinding consumerInstances(int consumerInstances) {
        this.consumerInstances = consumerInstances;
        LOGGER.info("Define <{}> consumers to the queue <{}>", consumerInstances, queue);
        return this;
    }

    /**
     * Set the consumer instance to the given queue.
     */
    public QueueBinding prefetchMessageCount(int prefetchMessageCount) {
        this.prefetchMessageCount = prefetchMessageCount;
        LOGGER.info("Define <{}> prefetchMessageCount to the queue <{}>", prefetchMessageCount, queue);
        return this;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public String getQueue() {
        return queue;
    }

    public boolean isAutoAck() {
        return autoAck;
    }

    public int getConsumerInstances() {
        return consumerInstances;
    }

    public int getPrefetchMessageCount() {
        return prefetchMessageCount;
    }
}

