package io.github.jlmc.cdi.adapter.amqp.rabbit.events.text;


import java.util.Map;

public interface TextPlainConvertersConfigurator {

    Map<Class<?>, TextPlainConverter<?>> configure();
}
