package io.github.jlmc.cdi.adapter.amqp.rabbit.publisher;

/**
 * <p>Delivery options define a more fine granulated
 * way how messages should be handled in the broker.</p>
 */
public enum DeliveryOptions {
    /**
     * Delivery option mandatory means a message MUST be delivery to AT LEAST ONE queue
     */
    MANDATORY,
    /**
     * Delivery option mandatory means a message MUST be delivered to AT LEAST ONE consumer
     */
    IMMEDIATE,
    /**
     * No delivery option used
     */
    NONE
}
