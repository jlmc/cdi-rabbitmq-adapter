package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;

abstract class AbstractExchange extends AbstractDeclarable implements Exchange {

    private final String name;
    private final boolean durable;
    private final boolean autoDelete;
    private boolean delayed;
    private boolean internal;

    protected AbstractExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        super(arguments);
        this.name = name;
        this.durable = durable;
        this.autoDelete = autoDelete;
    }

    protected AbstractExchange(String name, boolean durable, boolean autoDelete) {
        this(name, durable, autoDelete, Map.of());
    }

    protected AbstractExchange(String name) {
        this(name, false, false);
    }

    public abstract String getType();

    public String toString() {
        return "Exchange [name=" + this.name
                + ", type=" + this.getType()
                + ", durable=" + this.durable
                + ", autoDelete=" + this.autoDelete
                + ", internal=" + this.internal +"]";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDurable() {
        return durable;
    }

    @Override
    public boolean isAutoDelete() {
        return autoDelete;
    }

    @Override
    public boolean isDelayed() {
        return delayed;
    }

    @Override
    public boolean isInternal() {
        return internal;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }
}
