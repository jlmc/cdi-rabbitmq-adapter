package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;

/**
 * Simple container collecting information to describe a binding. Takes String destination and exchange names as
 * arguments to facilitate wiring using code based configuration. Can be used via a {@link BindingBuilder}.
 */
public class Binding extends AbstractDeclarable {

    private final String destination;
    private final String exchange;
    private final String routingKey;
    private final DestinationType destinationType;

    public Binding(String destination,
                   DestinationType destinationType,
                   String exchange,
                   String routingKey,
                   Map<String, Object> arguments) {

        super(arguments);
        this.destination = destination;
        this.destinationType = destinationType;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public String getDestination() {
        return this.destination;
    }

    public DestinationType getDestinationType() {
        return this.destinationType;
    }

    public String getExchange() {
        return this.exchange;
    }

    public String getRoutingKey() {
        return this.routingKey;
    }

    public boolean isDestinationQueue() {
        return DestinationType.QUEUE.equals(this.destinationType);
    }

    @Override
    public String toString() {
        return "Binding [destination=" + this.destination
                + ", exchange=" + this.exchange
                + ", routingKey=" + this.routingKey
                + ", arguments=" + getArguments() + "]";
    }

    public enum DestinationType {

        /**
         * Queue destination.
         */
        QUEUE,

        /**
         * Exchange destination.
         */
        EXCHANGE;
    }
}
