package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;

public class DirectExchange extends AbstractExchange {

    public static final DirectExchange DEFAULT = new DirectExchange("");

    public DirectExchange(String name) {
        super(name);
    }

    public DirectExchange(String name, boolean durable, boolean autoDelete) {
        super(name, durable, autoDelete);
    }

    public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        super(name, durable, autoDelete, arguments);
    }

    @Override
    public final String getType() {
        return ExchangeType.DIRECT.getType();
    }
}
