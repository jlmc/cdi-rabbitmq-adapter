package io.github.jlmc.cdi.adapter.amqp.rabbit;

public interface EventReader {

    <T> T read(byte[] bytes, Class<T> type);

}
