package io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.configs;

import io.github.jlmc.cdi.adapter.amqp.rabbit.BindingsConfigurator;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventBindingBuilder;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ExchangeBinding;
import io.github.jlmc.cdi.adapter.amqp.rabbit.QueueBinding;
import io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.events.recived.DeliveryEvent;
import io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.events.sent.SentEvent;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ExampleBindingsConfigurator implements BindingsConfigurator {

    @Override
    public List<ExchangeBinding> publisherBindings() {
        ExchangeBinding exchangeBinding =
                EventBindingBuilder.bind(SentEvent.class)
                                   .toExchange("x.direct")
                                   .withRoutingKey("helloA")
                                   .withPersistentMessages()
                                   //.withPublisherConfirms()
                                   .withPublisherTransactions();
        return List.of(exchangeBinding);
    }

    @Override
    public List<QueueBinding> consumerBindings() {
        QueueBinding queueBinding =
                EventBindingBuilder.bind(DeliveryEvent.class)
                                   .fromQueue("q.queueA")
                                   .consumerInstances(3)
                                   .autoAck()
                                   .prefetchMessageCount(5);
        return List.of(queueBinding);
    }
}
