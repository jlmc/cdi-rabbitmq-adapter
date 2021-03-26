package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;

/**
 * Simple container collecting information to describe a fanout exchange. Used in conjunction with administrative operations
 */
public class FanoutExchange extends AbstractExchange {

    public FanoutExchange(String name) {
        super(name);
    }

    public FanoutExchange(String name, boolean durable, boolean autoDelete) {
        super(name, durable, autoDelete);
    }

    public FanoutExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        super(name, durable, autoDelete, arguments);
    }

    @Override
    public final String getType() {
        return ExchangeType.FANOUT.getType();
    }

}

