package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DeclarablesTest {

    @Test
    void createDeclables() {

        DirectExchange direct = ExchangeBuilder.directExchange("x.direct").durable(true).build();
        FanoutExchange fanout = ExchangeBuilder.fanoutExchange("x.fanout").durable(true).build();

        Queue queueA = QueueBuilder.durable("q.queueA").deadLetterExchange(fanout.getName()).deadLetterRoutingKey("A").build();
        Queue queueB = QueueBuilder.durable("q.queueB").deadLetterExchange(fanout.getName()).deadLetterRoutingKey("B").build();


        Binding helloA =
                BindingBuilder.bind(queueA)
                              .to(direct)
                              .with("helloA");
        Binding helloB =
                BindingBuilder.bind(queueB)
                              .to(direct)
                              .with("helloB");

        Declarables declarables = new Declarables(
                direct,
                fanout,
                queueA,
                queueB,
                helloA
        );

        List<Queue> queues = declarables.getDeclarablesByType(Queue.class);
        List<Exchange> exchanges = declarables.getDeclarablesByType(Exchange.class);
        List<Binding> bindings = declarables.getDeclarablesByType(Binding.class);

        Assertions.assertNotNull(queues);
        Assertions.assertNotNull(exchanges);
        Assertions.assertNotNull(bindings);

        Assertions.assertEquals(2, exchanges.size());
        Assertions.assertEquals(2, queues.size());
        Assertions.assertEquals(1, bindings.size());

        Assertions.assertTrue(queues.contains(queueA));
        Assertions.assertTrue(queues.contains(queueB));
        Assertions.assertTrue(exchanges.contains(direct));
        Assertions.assertTrue(exchanges.contains(fanout));
        Assertions.assertTrue(bindings.contains(helloA));
        Assertions.assertFalse(bindings.contains(helloB));
    }
}