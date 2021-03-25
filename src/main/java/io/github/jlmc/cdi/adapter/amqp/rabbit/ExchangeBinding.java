package io.github.jlmc.cdi.adapter.amqp.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import io.github.jlmc.cdi.adapter.amqp.rabbit.publisher.DeliveryOptions;
import io.github.jlmc.cdi.adapter.amqp.rabbit.publisher.PublisherReliability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Configures and stores the binding between an event class and an exchange.
 */
public final class ExchangeBinding {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeBinding.class);

    private final Class<?> eventType;
    private final String exchange;
    private String routingKey;
    private boolean persistent;

    private PublisherReliability reliability = PublisherReliability.NONE;
    private DeliveryOptions deliveryOptions = DeliveryOptions.NONE;
    private AMQP.BasicProperties basicProperties = MessageProperties.BASIC.builder()
                                                                          .contentType(ContentTypes.APPLICATION_JSON)
                                                                          .contentEncoding(StandardCharsets.UTF_8.name())
                                                                          .build();

    ExchangeBinding(Class<?> eventType, String exchange) {
        this.eventType = eventType;
        this.exchange = exchange;
    }

    public ExchangeBinding withRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        LOGGER.info("Routing key for CDI  event type {} set to {}", eventType.getSimpleName(), routingKey);
        return this;
    }

    /**
     * <p>Sets the flag for persisting messages on the broker after publishing.</p>
     *
     * <p>Persistent messages survive a broker failure and can be restored
     * after a broker shutdown.</p>
     */
    public ExchangeBinding withPersistentMessages() {
        this.persistent = true;
        LOGGER.info("Persistent messages enabled for event type <{}>", eventType.getSimpleName());
        return this;
    }

    /**
     * Sets delivery options to be used for message publishing to immediate.
     *
     * @see DeliveryOptions#IMMEDIATE
     */
    public ExchangeBinding withImmediateDelivery() {
        return setDeliveryOptions(DeliveryOptions.IMMEDIATE);
    }

    /**
     * Sets delivery options to be used for message publishing to mandatory.
     *
     * @see DeliveryOptions#MANDATORY
     */
    public ExchangeBinding withMandatoryDelivery() {
        return setDeliveryOptions(DeliveryOptions.MANDATORY);
    }

    /**
     * Sets the given basic properties to be used for message publishing.
     *
     * @param basicProperties The basic properties
     */
    public ExchangeBinding withProperties(AMQP.BasicProperties basicProperties) {
        this.basicProperties = basicProperties;
        LOGGER.info("Publisher properties for event type <{}> set to <{}>", eventType.getSimpleName(), basicProperties.toString());
        return this;
    }

    /**
     * Sets the content Type basic property to be used in the messages.
     *
     * @param contentType The
     */
    public ExchangeBinding withContentType(String contentType) {
        withProperties(this.basicProperties.builder().contentEncoding(contentType).build());
        return this;
    }

    public ExchangeBinding withPublisherConfirms() {
        return setPublisherReliability(PublisherReliability.CONFIRMED);
    }

    /**
     * Sets the reliability to be used for message publishing to transactional.
     *
     * @see PublisherReliability#TRANSACTIONAL
     */
    public ExchangeBinding withPublisherTransactions() {
        return setPublisherReliability(PublisherReliability.TRANSACTIONAL);
    }

    private ExchangeBinding setPublisherReliability(PublisherReliability reliability) {
        if (this.reliability != PublisherReliability.NONE) {
            LOGGER.warn("Publisher reliability for event type <{}> is overridden: <{}>", eventType.getSimpleName(), reliability);
        }
        this.reliability = reliability;
        LOGGER.info("Publisher reliability for event type <{}> set to <{}>", eventType.getSimpleName(), reliability);
        return this;
    }

    private ExchangeBinding setDeliveryOptions(DeliveryOptions deliveryOptions) {
        if (this.deliveryOptions != DeliveryOptions.NONE) {
            LOGGER.warn("Delivery options for event type {} are overridden: <{}>", eventType.getSimpleName(), deliveryOptions);
        }
        this.deliveryOptions = deliveryOptions;
        LOGGER.info("Delivery options for event type <{}> set to <{}>", eventType.getSimpleName(), deliveryOptions);
        return this;
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

    public Class<?> getEventType() {
        return eventType;
    }
}
