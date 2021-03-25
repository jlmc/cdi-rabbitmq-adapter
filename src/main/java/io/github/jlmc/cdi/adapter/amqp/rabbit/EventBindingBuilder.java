package io.github.jlmc.cdi.adapter.amqp.rabbit;

/**
 * <p>Starting point for binding an event.</p>
 *
 * <p><b>Binding fired events to be published to an exchange:</b></p>
 * <p>bind(MyEvent.class).toExchange("my.exchange");</p>
 * <p><b>Binding consuming from a queue to fire an event:</b></p>
 * <p>bind(MyEvent.class).toQueue("my.queue");</p>
 */
public final class EventBindingBuilder {

    private final Class<?> eventType;

    private EventBindingBuilder(Class<?> eventType) {
        this.eventType = eventType;
    }

    public static EventBindingBuilder bind(Class<?> eventType) {
        return new EventBindingBuilder(eventType);
    }


    /**
     * Binds an event to the given queue. On initialization, a
     * consumer is going to be registered at the broker that
     * is going to fire an event of the bound event type
     * for every consumed message.
     *
     * @param queue The queue
     */
    public QueueBinding fromQueue(String queue) {
        return new QueueBinding(eventType, queue);
    }

    /**
     * Binds an event to the given exchange. After initialization,
     * all fired events of the bound event type are going to
     * be published to the exchange.
     *
     * @param exchange The exchange
     */
    public ExchangeBinding toExchange(String exchange) {
        return new ExchangeBinding(eventType, exchange);
    }
}
