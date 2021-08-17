![Java CI with Maven](https://github.com/jlmc/cdi-rabbitmq-adapter/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.jlmc/cdi-rabbitmq-adapter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.jlmc/cdi-rabbitmq-adapter)


# cdi-rabbitmq-adapter

`cdi-rabbitmq-adapter` it is a convenient java library to make easy to integration with the rabbitMQ in project that use CDI 2.0 or highers it may be a Java Se or a Jakarta EE application. 


## Features 

- connection factory for long living single connections
- managed simple, confirmed and transactional publishers that recover from connection loss
- managed consumers that recover from connection loss and re-attach to the broker
- create the rabbitMQ Schema

- convenient integration for JakartaEE8/CDI applications
- publishing of AMQP messages to exchanges for CDI events
- consuming of AMQP messages from queues as CDI events

## Overview

The basic principle that allows to integrate rabbitMQ in a **JakartaEE** environment is to use the facilities of the **CDI** (Context Dependency Injection).
Basically we need:

- to fire CDI events remotely, bind them to be published as messages to broker exchanges
- to observe CDI events remotely, bind them to be consumed as messages from broker queues


### Configure the connection factory

A single connection factory always provides the same connection on calling newConnection() as long as the connection persists. 
A new connection is established as soon as the current connection is lost.

SingleConnectionFactory extends ConnectionFactory from the RabbitMQ standard library and is used just the same way as the factory from the standard library. The only difference: From now on you don't have to care about too many connections being established to a broker any more.

We must configure the Connection Factory using an implementation of `ConnectionFactoryConfigurationsConfigurator`, the definition of the class is mandatory:

```java
import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionFactoryConfigurations;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionFactoryConfigurationsConfigurator;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DemoFactoryConfigurationsConfigurator implements ConnectionFactoryConfigurationsConfigurator {

    @Resource(name = "DefaultManagedExecutorService", lookup = "java:comp/DefaultManagedExecutorService")
    ManagedExecutorService mes;

    @Resource(name = "DefaultManagedScheduledExecutorService", lookup = "java:comp/DefaultManagedScheduledExecutorService")
    ManagedScheduledExecutorService mses;

    @Override
    public ConnectionFactoryConfigurations configure() {

        return ConnectionFactoryConfigurations.defaults()
                                              .setUsername("admin")
                                              .setPassword("admin")
                                              .setVirtualHost("example")
                                              .setExecutorService(mes)
                                              .setHeartbeatExecutor(mses);
    }
}

```

### Declaring Collections of Exchanges, Queues, and Bindings

We can create the rabbitMQ schema by implementing the `DeclarablesConfigurator`

```java
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
public class DemoDeclarablesConfigurator implements DeclarablesConfigurator {

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
```

### Binding the CDI Events to the exchanges

To bind events, first create an implementation of `BindingsConfigurator` and override its `publisherBindings()` method:

```java
import io.github.jlmc.cdi.adapter.amqp.rabbit.BindingsConfigurator;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventBindingBuilder;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ExchangeBinding;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DemoPublisherBindingsConfigurator implements BindingsConfigurator {

    @Override
    public List<ExchangeBinding> publisherBindings() {
            ExchangeBinding exchangeBinding =
                    EventBindingBuilder.bind(MealBookedEvent.class)
                                       .toExchange("x.direct")
                                       .withRoutingKey("helloA")
                                       .withPersistentMessages()
                                       .withPublisherConfirms();
                                       //.withPublisherTransactions();
        
            // We can map how many ExchangeBinding we need...
        
            return List.of(exchangeBinding);
    }
}
```

Firing a CDI event is going to publish a message to the given exchange and routing key:

```java
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class MealBookingCommandService {

    @Inject
    private Event<MealBookedEvent> mealBookedEventControl;

    public void bookCargo(BookCargoCommand bookCargoCommand) {

        CargoBookedEvent cargoBookedEvent = new CargoBookedEvent();
        cargoBookedEvent.setId("1234");

        mealBookedEventControl.fire(cargoBookedEvent);
    }
}
```
This is going to publish the fired event to local observers of `MealBookedEvent` and is also going to publish a message to the exchange `x.direct` with routing key `hello` as we have defined it in the binding.


### Bind the queues messages to CDI Events

Binding an event to a queue for consuming events works the same. We should first create an implementation of `BindingsConfigurator` and override its `consumerBindings()` method:

```java
import io.github.jlmc.cdi.adapter.amqp.rabbit.BindingsConfigurator;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventBindingBuilder;
import io.github.jlmc.cdi.adapter.amqp.rabbit.QueueBinding;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DemoConsumerBindingsConfigurator implements BindingsConfigurator {

    @Override
    public List<QueueBinding> consumerBindings() {
        QueueBinding queueBinding =
                EventBindingBuilder.bind(MealBookedEvent.class)
                                   .fromQueue("q.queueA")
                                   .consumerInstances(3)
                                   .autoAck()
                                   .prefetchMessageCount(5);
        return List.of(queueBinding);
    }
}
```

Now, CDI observers of the bound event are going to consume messages from `q.queueA` in form of the bound event:

```java
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class MealBookedEventConsumer {

    public void onMealBookedEvent(@Observes MealBookedEvent event) {
        
        // business implementation ...
        
    }
}
```