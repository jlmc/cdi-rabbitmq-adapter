package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import com.rabbitmq.client.BuiltinExchangeType;

/**
 * Constants for the standard Exchange type names.
 */
public enum ExchangeType {

    DIRECT(BuiltinExchangeType.DIRECT),
    FANOUT(BuiltinExchangeType.FANOUT),
    TOPIC(BuiltinExchangeType.TOPIC),
    HEADERS(BuiltinExchangeType.HEADERS);

    private final BuiltinExchangeType type;

    ExchangeType(BuiltinExchangeType type) {
        this.type = type;
    }

    public String getType() {
        return type.getType();
    }

    public BuiltinExchangeType getBuiltinExchangeType() {
        return type;
    }
}
