package io.github.jlmc.cdi.adapter.amqp.rabbit;

public interface EventWriter {

    <T> byte[] write(T event, Class<?> type);
}
