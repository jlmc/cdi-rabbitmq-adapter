package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;

/**
 * Builder providing a fluent API for building Exchanges.
 */
public class ExchangeBuilder extends AbstractBuilder {

    private final String name;
    private final ExchangeType type;
    private boolean autoDelete;
    private boolean durable = true;
    private boolean delayed;
    private boolean internal;

    private ExchangeBuilder(String name, ExchangeType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Return a {@link DirectExchange} builder.
     * @param name the name.
     * @return the builder.
     */
    public static ExchangeBuilder directExchange(String name) {
        return new ExchangeBuilder(name, ExchangeType.DIRECT);
    }

    /**
     * Return a {@link FanoutExchange} builder.
     * @param name the name.
     * @return the builder.
     */
    public static ExchangeBuilder fanoutExchange(String name) {
        return new ExchangeBuilder(name, ExchangeType.FANOUT);
    }

    /**
     * Return a {@link TopicExchange} builder.
     * @param name the name.
     * @return the builder.
     */
    public static ExchangeBuilder topicExchange(String name) {
        return new ExchangeBuilder(name, ExchangeType.TOPIC);
    }

    /**
     * Return a {@link HeadersExchange} builder.
     * @param name the name.
     * @return the builder.
     */
    public static ExchangeBuilder headersExchange(String name) {
        return new ExchangeBuilder(name, ExchangeType.HEADERS);
    }

    public ExchangeBuilder internal() {
        this.internal = true;
        return this;
    }

    public ExchangeBuilder delayed() {
        this.delayed = true;
        return this;
    }

    /**
     * Set the auto delete flag.
     * @return the builder.
     */
    public ExchangeBuilder autoDelete() {
        this.autoDelete = true;
        return this;
    }

    /**
     * Set the durable flag.
     * @param isDurable the durable flag (default true).
     * @return the builder.
     */
    public ExchangeBuilder durable(boolean isDurable) {
        this.durable = isDurable;
        return this;
    }

    /**
     * Add an argument.
     * @param key the argument key.
     * @param value the argument value.
     * @return the builder.
     */
    public ExchangeBuilder withArgument(String key, Object value) {
        getOrCreateArguments().put(key, value);
        return this;
    }

    /**
     * Add the arguments.
     * @param arguments the arguments map.
     * @return the builder.
     */
    public ExchangeBuilder withArguments(Map<String, Object> arguments) {
        this.getOrCreateArguments().putAll(arguments);
        return this;
    }

    public ExchangeBuilder alternate(String exchange) {
        return withArgument("alternate-exchange", exchange);
    }

    @SuppressWarnings("unchecked")
    public <T extends Exchange> T build() {
        AbstractExchange exchange;
        if (ExchangeType.DIRECT.equals(this.type)) {
            exchange = new DirectExchange(this.name, this.durable, this.autoDelete, getArguments());
        }
        else if (ExchangeType.FANOUT.equals(this.type)) {
            exchange = new FanoutExchange(this.name, this.durable, this.autoDelete, getArguments());
        }
        else if (ExchangeType.TOPIC.equals(this.type)) {
            exchange = new TopicExchange(this.name, this.durable, this.autoDelete, getArguments());
        }
        else if (ExchangeType.HEADERS.equals(this.type)) {
            exchange = new HeadersExchange(this.name, this.durable, this.autoDelete, getArguments());
        }
        else {
            throw new IllegalStateException("No Exchange type defined!!!");
        }

        exchange.setInternal(this.internal);
        exchange.setDelayed(this.delayed);

        return (T) exchange;
    }

}
