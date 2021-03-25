package io.github.jlmc.cdi.adapter.amqp.rabbit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class QueueBindingTest {

    static final String Q_FOO = "q.foo";

    @Test
    void createMinimalisticQueueBinding() {
        QueueBinding queueBinding =
                EventBindingBuilder.bind(Foo.class)
                                   .fromQueue(Q_FOO);

        Assertions.assertNotNull(queueBinding);
        Assertions.assertEquals(Q_FOO, queueBinding.getQueue());
        Assertions.assertFalse(queueBinding.isAutoAck());
        Assertions.assertEquals(1, queueBinding.getConsumerInstances());
        Assertions.assertEquals(0, queueBinding.getPrefetchMessageCount());
    }

    @Test
    void createQueueBindingWithALlParameters() {
        QueueBinding queueBinding =
                EventBindingBuilder.bind(Foo.class)
                                   .fromQueue(Q_FOO)
                                   .autoAck()
                                   .consumerInstances(123)
                                   .prefetchMessageCount(3);

        Assertions.assertNotNull(queueBinding);
        Assertions.assertEquals(Q_FOO, queueBinding.getQueue());
        Assertions.assertTrue(queueBinding.isAutoAck());
        Assertions.assertEquals(123, queueBinding.getConsumerInstances());
        Assertions.assertEquals(3, queueBinding.getPrefetchMessageCount());
    }

    static class Foo {}
}