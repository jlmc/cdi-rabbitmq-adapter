package io.github.jlmc.cdi.adapter.amqp.rabbit.events.text;

public interface TextPlainConverter <T> {

    T fromText(String text);

    String toText(T object);
}
