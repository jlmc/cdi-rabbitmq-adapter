package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;

public class TopicExchange extends AbstractExchange {

    public TopicExchange(String name) {
        super(name);
    }

    public TopicExchange(String name, boolean durable, boolean autoDelete) {
        super(name, durable, autoDelete);
    }

    public TopicExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        super(name, durable, autoDelete, arguments);
    }

    @Override
    public final String getType() {
        return ExchangeType.TOPIC.getType();
    }
}
