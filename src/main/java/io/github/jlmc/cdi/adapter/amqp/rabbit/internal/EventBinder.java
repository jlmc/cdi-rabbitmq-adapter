package io.github.jlmc.cdi.adapter.amqp.rabbit.internal;

import io.github.jlmc.cdi.adapter.amqp.rabbit.BindingsConfigurator;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ExchangeBinding;
import io.github.jlmc.cdi.adapter.amqp.rabbit.QueueBinding;
import io.github.jlmc.cdi.adapter.amqp.rabbit.consumer.ConsumerConfiguration;
import io.github.jlmc.cdi.adapter.amqp.rabbit.consumer.ConsumerContainer;
import io.github.jlmc.cdi.adapter.amqp.rabbit.consumer.EventConsumer;
import io.github.jlmc.cdi.adapter.amqp.rabbit.events.EventReaderHolder;
import io.github.jlmc.cdi.adapter.amqp.rabbit.publisher.EventPublisher;
import io.github.jlmc.cdi.adapter.amqp.rabbit.publisher.EventPublisherEntryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class EventBinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBinder.class);

    @Inject
    Instance<BindingsConfigurator> bindingsConfigurators;

    @Inject
    EventPublisher eventPublisher;

    @Inject
    Event<Object> remoteEventControl;

    @Inject
    Instance<Object> remoteEventPool;

    @Inject
    ConsumerContainer consumerContainer;

    @Inject
    EventReaderHolder eventReaderHolder;

    private final Set<ExchangeBinding> exchangeBindings = new HashSet<>();
    private final Set<QueueBinding> queueBindings = new HashSet<>();

    /**
     * <p>Extend {@link EventBinder} and implement this method to
     * create the event bindings for your application.</p>
     *
     * <p>See
     * <a href="https://github.com/zanox/rabbiteasy#using-event-binders">the documentation</a>
     * for further information</p>
     */
    protected void bindEvents() {
        for (BindingsConfigurator bindingsConfigurator : bindingsConfigurators) {
            List<ExchangeBinding> exchangeBindings = bindingsConfigurator.publisherBindings();
            this.exchangeBindings.addAll(exchangeBindings);


            List<QueueBinding> queueBindings = bindingsConfigurator.consumerBindings();
            this.queueBindings.addAll(queueBindings);
        }
    }

    /**
     * <p>Initializes the event binder and effectively enables all bindings
     * created in {@link #bindEvents()}.</p>
     *
     * <p>Inject your event binder implementation at the beginning of
     * your application's lifecycle and call this method. In web applications,
     * a good place for this is a ServletContextListener.</p>
     *
     * <p>After this method was successfully called, consumers are registered at
     * the target broker for every queue binding. Also, for every exchange binding
     * messages are going to be published to the target broker.</p>
     *
     * <p>See
     * <a href="https://github.com/zanox/rabbiteasy#using-event-binders">the documentation</a>
     * for further information</p>
     *
     */
    public void initialize() {
        bindEvents();

        processExchangeBindings();

        processQueueBindings();

        consumerContainer.startAllConsumers();
    }

    private void processExchangeBindings() {
        for (ExchangeBinding exchangeBinding : exchangeBindings) {
            bindExchange(exchangeBinding);
        }
        exchangeBindings.clear();
    }

    private void bindExchange(ExchangeBinding exchangeBinding){

        EventPublisherEntryConfiguration configuration =
                new EventPublisherEntryConfiguration(exchangeBinding.getExchange(),
                                                     exchangeBinding.getRoutingKey(),
                                                     exchangeBinding.isPersistent(),
                                                     exchangeBinding.getReliability(),
                                                     exchangeBinding.getDeliveryOptions(),
                                                     exchangeBinding.getBasicProperties()
                        );

        this.eventPublisher.addEvent(exchangeBinding.getEventType(), configuration);
        LOGGER.info("Binding between exchange <{}> and event type <{}> activated",
                exchangeBinding.getExchange(), exchangeBinding.getEventType().getSimpleName());
    }


    void processQueueBindings() {
        for (QueueBinding queueBinding : queueBindings) {
            bindQueue(queueBinding);
        }
        queueBindings.clear();
    }

    void bindQueue(QueueBinding queueBinding) {
        @SuppressWarnings("unchecked")
        Event<Object> eventControl = (Event<Object>) remoteEventControl.select(queueBinding.getEventType());
        @SuppressWarnings("unchecked")
        Instance<Object> eventPool = (Instance<Object>) remoteEventPool.select(queueBinding.getEventType());

        EventConsumer consumer = new EventConsumer(eventControl, eventPool, eventReaderHolder::findReader);

        ConsumerConfiguration consumerConfiguration =
                new ConsumerConfiguration(queueBinding.getQueue(), queueBinding.isAutoAck())
                        .prefetchMessageCount(queueBinding.getPrefetchMessageCount()).consumerInstances(queueBinding.getConsumerInstances())
                        .eventType(queueBinding.getEventType());

        consumerContainer.addConsumer(consumer, consumerConfiguration);

        LOGGER.info("Binding between queue {} and event type {} activated", queueBinding.getQueue(), queueBinding.getEventType().getSimpleName());
    }

}
