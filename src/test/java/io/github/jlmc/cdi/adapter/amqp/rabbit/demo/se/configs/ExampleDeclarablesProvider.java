package io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.configs;

import io.github.jlmc.cdi.adapter.amqp.rabbit.DeclarablesConfigurator;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Binding;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.BindingBuilder;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Declarables;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.DirectExchange;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.ExchangeBuilder;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.FanoutExchange;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Queue;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.QueueBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExampleDeclarablesProvider implements DeclarablesConfigurator {

    @Override
    public Declarables configure() {

        DirectExchange exchangeDirect =
                ExchangeBuilder.directExchange("x.direct")
                               .durable(true)
                               .build();
        FanoutExchange exchangeFanout =
                ExchangeBuilder.fanoutExchange("x.fanout")
                               .durable(true)
                               .build();

        Queue queueA =
                QueueBuilder.durable("q.queueA")
                            .deadLetterExchange(exchangeFanout.getName())
                            .deadLetterRoutingKey("A")
                            .build();
        Queue queueB =
                QueueBuilder.durable("q.queueB")
                            .deadLetterExchange(exchangeFanout.getName())
                            .deadLetterRoutingKey("B")
                            .build();

        Binding bindingQueueHelloA =
                BindingBuilder.bind(queueA)
                              .to(exchangeDirect)
                              .with("helloA");
        Binding bindingQueueHelloB =
                BindingBuilder.bind(queueB)
                              .to(exchangeDirect)
                              .with("helloB");

        return new Declarables(
                exchangeDirect,
                exchangeFanout,
                queueA,
                queueB,
                bindingQueueHelloA,
                bindingQueueHelloB
        );
    }
}
