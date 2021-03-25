package io.github.jlmc.cdi.adapter.amqp.rabbit.consumer;

/**
 * A consumer configuration holds parameters to be set before enabling a consumer to consume messages from the message broker.
 */
public class ConsumerConfiguration {

    public static final int DEFAULT_AMOUNT_OF_INSTANCES = 1;
    public static final int UNLIMITED_PREFETCH_MESSAGE_COUNT = 0;

    private final String queueName;
    private boolean autoAck = false;
    private int prefetchMessageCount = UNLIMITED_PREFETCH_MESSAGE_COUNT;
    private int consumerInstances = DEFAULT_AMOUNT_OF_INSTANCES;
    private Class<?> eventType;

    public ConsumerConfiguration(String queueName) {
        this.queueName = queueName;
    }

    public ConsumerConfiguration(String queueName, boolean autoAck) {
        this.queueName = queueName;
        this.autoAck = autoAck;
    }

    public ConsumerConfiguration consumerInstances(int consumerInstances) {
        this.consumerInstances = consumerInstances;
        return this;
    }

    public ConsumerConfiguration prefetchMessageCount(int prefetchMessageCount) {
        this.prefetchMessageCount = prefetchMessageCount;
        return this;
    }

    public <T> ConsumerConfiguration eventType(Class<T> eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getQueueName() {
        return queueName;
    }

    public boolean isAutoAck() {
        return autoAck;
    }


    public int getPrefetchMessageCount() {
        return prefetchMessageCount;
    }

    public int getConsumerInstances() {
        return consumerInstances;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "ConsumerConfiguration{" +
                "queueName='" + queueName + '\'' +
                ", autoAck=" + autoAck +
                ", prefetchMessageCount=" + prefetchMessageCount +
                '}';
    }
}
