package io.github.jlmc.cdi.adapter.amqp.rabbit;

import io.github.jlmc.cdi.adapter.amqp.rabbit.publisher.DeliveryOptions;
import io.github.jlmc.cdi.adapter.amqp.rabbit.publisher.PublisherReliability;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExchangeBindingTest {

    @Test
    void createMinimalisticExchangeBinding() {
        ExchangeBinding exchangeBinding =
                EventBindingBuilder.bind(Foo.class)
                                   .toExchange("my.exchange")
                                   .withRoutingKey("my.routing.key");

        Assertions.assertNotNull(exchangeBinding);
        Assertions.assertEquals("my.exchange", exchangeBinding.getExchange());
        Assertions.assertEquals("my.routing.key", exchangeBinding.getRoutingKey());
        Assertions.assertEquals(Foo.class, exchangeBinding.getEventType());
        Assertions.assertFalse(exchangeBinding.isPersistent());
        Assertions.assertEquals(PublisherReliability.NONE, exchangeBinding.getReliability());
        Assertions.assertEquals(DeliveryOptions.NONE, exchangeBinding.getDeliveryOptions());
    }

    @Test
    void createExchangeBindingWithALlParameters() {
        ExchangeBinding exchangeBinding =
                EventBindingBuilder.bind(Foo.class)
                                   .toExchange("my.exchange")
                                   .withRoutingKey("my.routing.key")
                                   .withPersistentMessages()
                                   .withImmediateDelivery()
                                   .withPublisherConfirms();

        Assertions.assertNotNull(exchangeBinding);
        Assertions.assertEquals("my.exchange", exchangeBinding.getExchange());
        Assertions.assertEquals("my.routing.key", exchangeBinding.getRoutingKey());
        Assertions.assertEquals(Foo.class, exchangeBinding.getEventType());
        Assertions.assertTrue(exchangeBinding.isPersistent());
        Assertions.assertEquals(PublisherReliability.CONFIRMED, exchangeBinding.getReliability());
        Assertions.assertEquals(DeliveryOptions.IMMEDIATE, exchangeBinding.getDeliveryOptions());
    }

    private static class Foo {
    }
}