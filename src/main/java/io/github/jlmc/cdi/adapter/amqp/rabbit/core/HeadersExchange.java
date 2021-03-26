package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;

/**
 * Headers exchange.
 */
public class HeadersExchange extends AbstractExchange {

    public HeadersExchange(String name) {
        super(name);
    }

    public HeadersExchange(String name, boolean durable, boolean autoDelete) {
        super(name, durable, autoDelete);
    }

    public HeadersExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        super(name, durable, autoDelete, arguments);
    }

    @Override
    public final String getType() {
        return ExchangeType.HEADERS.getType();
    }

}
