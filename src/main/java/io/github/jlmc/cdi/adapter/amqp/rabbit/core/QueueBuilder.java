package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;
import java.util.Objects;

public final class QueueBuilder extends AbstractBuilder {

    private final String name;

    private boolean durable;

    private boolean exclusive;

    private boolean autoDelete;

    /**
     * Creates a builder for a durable queue.
     * @param name the name of the queue.
     * @return the QueueBuilder instance.
     */
    public static QueueBuilder durable(String name) {
        Objects.requireNonNull(name, "'name' cannot be null");
        return new QueueBuilder(name).setDurable();
    }

    /**
     * Creates a builder for a non-durable (transient) queue.
     * @param name the name of the queue.
     * @return the QueueBuilder instance.
     */
    public static QueueBuilder nonDurable(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");
        return new QueueBuilder(name);
    }

    private QueueBuilder(String name) {
        this.name = name;
    }

    private QueueBuilder setDurable() {
        this.durable = true;
        return this;
    }

    /**
     * The final queue will be exclusive.
     * @return the QueueBuilder instance.
     */
    public QueueBuilder exclusive() {
        this.exclusive = true;
        return this;
    }

    /**
     * The final queue will auto delete.
     * @return the QueueBuilder instance.
     */
    public QueueBuilder autoDelete() {
        this.autoDelete = true;
        return this;
    }

    /**
     * The final queue will contain argument used to declare a queue.
     * @param key argument name
     * @param value argument value
     * @return the QueueBuilder instance.
     */
    public QueueBuilder withArgument(String key, Object value) {
        getOrCreateArguments().put(key, value);
        return this;
    }

    /**
     * The final queue will contain arguments used to declare a queue.
     * @param arguments the arguments map
     * @return the QueueBuilder instance.
     */
    public QueueBuilder withArguments(Map<String, Object> arguments) {
        getOrCreateArguments().putAll(arguments);
        return this;
    }

    /**
     * Set the message time-to-live after which it will be discarded, or routed to the
     * dead-letter-exchange, if so configured.
     * @param ttl the time to live (milliseconds).
     * @return the builder.
     * @see #deadLetterExchange(String)
     */
    public QueueBuilder ttl(int ttl) {
        return withArgument("x-message-ttl", ttl);
    }

    /**
     * Set the time that the queue can remain unused before being deleted.
     * @param expires the expiration (milliseconds).
     * @return the builder.
     */
    public QueueBuilder expires(int expires) {
        return withArgument("x-expires", expires);
    }

    /**
     * Set the number of (ready) messages allowed in the queue before it starts to drop
     * them.
     * @param count the number of (ready) messages allowed.
     * @return the builder.
     */
    public QueueBuilder maxLength(int count) {
        return withArgument("x-max-length", count);
    }

    /**
     * Set the total aggregate body size allowed in the queue before it starts to drop
     * them.
     * @param bytes the total aggregate body size.
     * @return the builder.
     */
    public QueueBuilder maxLengthBytes(int bytes) {
        return withArgument("x-max-length-bytes", bytes);
    }

    /**
     * Set the dead-letter exchange to which to route expired or rejected messages.
     * @param dlx the dead-letter exchange.
     * @return the builder.
     * @see #deadLetterRoutingKey(String)
     */
    public QueueBuilder deadLetterExchange(String dlx) {
        return withArgument("x-dead-letter-exchange", dlx);
    }

    /**
     * Set the routing key to use when routing expired or rejected messages to the
     * dead-letter exchange.
     * @param dlrk the dead-letter routing key.
     * @return the builder.
     * @see #deadLetterExchange(String)
     */
    public QueueBuilder deadLetterRoutingKey(String dlrk) {
        return withArgument("x-dead-letter-routing-key", dlrk);
    }

    /**
     * Set the maximum number if priority levels for the queue to support; if not set, the
     * queue will not support message priorities.
     * @param maxPriority the maximum priority.
     * @return the builder.
     */
    public QueueBuilder maxPriority(int maxPriority) {
        return withArgument("x-max-priority", maxPriority);
    }

    /**
     * Set the queue into lazy mode, keeping as many messages as possible on disk to
     * reduce RAM usage on the broker. if not set, the queue will keep an in-memory cache
     * to deliver messages as fast as possible.
     * @return the builder.
     */
    public QueueBuilder lazy() {
        return withArgument("x-queue-mode", "lazy");
    }

    /**
     * Set the 'x-single-active-consumer' queue argument.
     * @return the builder.
     */
    public QueueBuilder singleActiveConsumer() {
        return withArgument("x-single-active-consumer", true);
    }

    /**
     * Set the queue argument to declare a queue of type 'quorum' instead of 'classic'.
     * @return the builder.
     */
    public QueueBuilder quorum() {
        return withArgument("x-queue-type", "quorum");
    }

    /**
     * Set the delivery limit; only applies to quorum queues.
     * @param limit the limit.
     * @return the builder.
     * @see #quorum()
     */
    public QueueBuilder deliveryLimit(int limit) {
        return withArgument("x-delivery-limit", limit);
    }

    /**
     * Builds a final queue.
     * @return the Queue instance.
     */
    public Queue build() {
        return new Queue(this.name, this.durable, this.exclusive, this.autoDelete, getArguments());
    }


}

