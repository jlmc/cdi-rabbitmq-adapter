package io.github.jlmc.cdi.adapter.amqp.rabbit.publisher;

import com.rabbitmq.client.AMQP;

/**
 * A publisher configuration stores all important settings and options used for publishing and event.
 */
public class EventPublisherEntryConfiguration {

    private final String exchange;
    private final String routingKey;
    private final boolean persistent;
    private final PublisherReliability reliability;
    private final DeliveryOptions deliveryOptions;
    private final AMQP.BasicProperties basicProperties;

    public EventPublisherEntryConfiguration(String exchange,
                                            String routingKey,
                                            boolean persistent,
                                            PublisherReliability reliability,
                                            DeliveryOptions deliveryOptions,
                                            AMQP.BasicProperties basicProperties) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.persistent = persistent;
        this.reliability = reliability;
        this.deliveryOptions = deliveryOptions;
        this.basicProperties = basicProperties;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public PublisherReliability getReliability() {
        return reliability;
    }

    public DeliveryOptions getDeliveryOptions() {
        return deliveryOptions;
    }

    public AMQP.BasicProperties getBasicProperties() {
        return basicProperties;
    }

    @Override
    public String toString() {
        return "EventPublisherEntryConfiguration{" +
                "exchange='" + exchange + '\'' +
                ", routingKey='" + routingKey + '\'' +
                ", persistent=" + persistent +
                ", reliability=" + reliability +
                '}';
    }
}
