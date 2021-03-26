package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Binding.DestinationType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic builder class to create bindings for a more fluent API style in code based configuration.
 */
public class BindingBuilder {

    public static DestinationConfigurer bind(Queue queue) {
        return new DestinationConfigurer(queue.getName(), DestinationType.QUEUE);
    }

    public static DestinationConfigurer bind(Exchange exchange) {
        return new DestinationConfigurer(exchange.getName(), DestinationType.EXCHANGE);
    }

    private static Map<String, Object> createMapForKeys(String... keys) {
        Map<String, Object> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, null);
        }
        return map;
    }

    /**
     * General destination configurer.
     */
    public static final class DestinationConfigurer {

        protected final String name;

        protected final DestinationType type;

        DestinationConfigurer(String name, DestinationType type) {
            this.name = name;
            this.type = type;
        }

        public Binding to(FanoutExchange exchange) {
            return new Binding(this.name, this.type, exchange.getName(), "", new HashMap<String, Object>());
        }

        public DirectExchangeRoutingKeyConfigurer to(DirectExchange exchange) {
            return new DirectExchangeRoutingKeyConfigurer(this, exchange);
        }

        public TopicExchangeRoutingKeyConfigurer to(TopicExchange exchange) {
            return new TopicExchangeRoutingKeyConfigurer(this, exchange);
        }

        public GenericExchangeRoutingKeyConfigurer to(Exchange exchange) {
            return new GenericExchangeRoutingKeyConfigurer(this, exchange);
        }
    }

    private abstract static class AbstractRoutingKeyConfigurer {

        protected final DestinationConfigurer destination;

        protected final String exchange;

        AbstractRoutingKeyConfigurer(DestinationConfigurer destination, String exchange) {
            this.destination = destination;
            this.exchange = exchange;
        }
    }

    /**
     * Topic exchange routing key configurer.
     */
    public static final class TopicExchangeRoutingKeyConfigurer extends AbstractRoutingKeyConfigurer {

        TopicExchangeRoutingKeyConfigurer(DestinationConfigurer destination, TopicExchange exchange) {
            super(destination, exchange.getName());
        }

        public Binding with(String routingKey) {
            return new Binding(destination.name,
                    destination.type,
                    exchange, routingKey,
                    Collections.emptyMap());
        }

        public Binding with(Enum<?> routingKeyEnum) {
            return new Binding(destination.name,
                    destination.type,
                    exchange,
                    routingKeyEnum.toString(),
                    Collections.<String, Object>emptyMap());
        }
    }

    /**
     * Generic exchange routing key configurer.
     */
    public static final class GenericExchangeRoutingKeyConfigurer extends AbstractRoutingKeyConfigurer {

        GenericExchangeRoutingKeyConfigurer(DestinationConfigurer destination, Exchange exchange) {
            super(destination, exchange.getName());
        }

        public GenericArgumentsConfigurer with(String routingKey) {
            return new GenericArgumentsConfigurer(this, routingKey);
        }

        public GenericArgumentsConfigurer with(Enum<?> routingKeyEnum) {
            return new GenericArgumentsConfigurer(this, routingKeyEnum.toString());
        }

    }

    /**
     * Generic argument configurer.
     */
    public static class GenericArgumentsConfigurer {

        private final GenericExchangeRoutingKeyConfigurer configurer;

        private final String routingKey;

        public GenericArgumentsConfigurer(GenericExchangeRoutingKeyConfigurer configurer, String routingKey) {
            this.configurer = configurer;
            this.routingKey = routingKey;
        }

        public Binding and(Map<String, Object> map) {
            return new Binding(this.configurer.destination.name, this.configurer.destination.type, this.configurer.exchange,
                    this.routingKey, map);
        }

        public Binding noargs() {
            return new Binding(this.configurer.destination.name, this.configurer.destination.type, this.configurer.exchange,
                    this.routingKey, Collections.emptyMap());
        }

    }

    /**
     * Direct exchange routing key configurer.
     */
    public static final class DirectExchangeRoutingKeyConfigurer extends AbstractRoutingKeyConfigurer {

        DirectExchangeRoutingKeyConfigurer(DestinationConfigurer destination, DirectExchange exchange) {
            super(destination, exchange.getName());
        }

        public Binding with(String routingKey) {
            return new Binding(destination.name, destination.type, exchange, routingKey,
                    Collections.emptyMap());
        }

        public Binding with(Enum<?> routingKeyEnum) {
            return new Binding(destination.name, destination.type, exchange, routingKeyEnum.toString(),
                    Collections.emptyMap());
        }

        public Binding withQueueName() {
            return new Binding(destination.name, destination.type, exchange, destination.name,
                    Collections.emptyMap());
        }
    }

}
