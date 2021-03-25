package io.github.jlmc.cdi.adapter.amqp.rabbit;

import java.util.List;

public interface BindingsConfigurator {

    default List<ExchangeBinding> publisherBindings() {
        return List.of();
    }

    default List<QueueBinding> consumerBindings() {
        return List.of();
    }
}
