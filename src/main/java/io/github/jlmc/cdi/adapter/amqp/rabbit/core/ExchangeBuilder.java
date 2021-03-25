package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;

public class ExchangeBuilder extends AbstractBuilder {

    private final String name;
    private final ExchangeType type;
    private boolean autoDelete;
    private boolean durable;
    private boolean delayed;
    private boolean internal;

    public ExchangeBuilder(String name, ExchangeType type) {
        this.name = name;
        this.type = type;
    }

    public static ExchangeBuilder directExchange(String name) {
        return new ExchangeBuilder(name, ExchangeType.DIRECT);
    }

    public static ExchangeBuilder fanoutExchange(String name) {
        return new ExchangeBuilder(name, ExchangeType.FANOUT);
    }

    public static ExchangeBuilder topicExchange(String name) {
        return new ExchangeBuilder(name, ExchangeType.TOPIC);
    }

    public ExchangeBuilder internal() {
        this.internal = true;
        return this;
    }

    public ExchangeBuilder delayed() {
        this.delayed = true;
        return this;
    }

    public ExchangeBuilder autoDelete() {
        this.autoDelete = true;
        return this;
    }

    public ExchangeBuilder durable(boolean isDurable) {
        this.durable = isDurable;
        return this;
    }

    public ExchangeBuilder withArgument(String key, Object value) {
        getOrCreateArguments().put(key, value);
        return this;
    }

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

        else {

            throw new IllegalStateException("No Exchange type defined!!!");
        }


        exchange.setInternal(this.internal);
        exchange.setDelayed(this.delayed);

        return (T) exchange;
    }

}
