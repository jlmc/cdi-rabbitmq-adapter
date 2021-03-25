package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.Map;
import java.util.Objects;

public class Queue extends AbstractDeclarable {

    public static final String X_QUEUE_MASTER_LOCATOR = "x-queue-master-locator";

    private final String name;
    private final boolean durable;
    private final boolean exclusive;
    private final boolean autoDelete;

    /**
     * The queue is durable, non-exclusive and non auto-delete.
     *
     * @param name the name of the queue.
     */
    public Queue(String name) {
        this(name, true, false, false);
    }

    /**
     * Construct a new queue, given a name and durability flag. The queue is non-exclusive and non auto-delete.
     *
     * @param name    the name of the queue.
     * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
     */
    public Queue(String name, boolean durable) {
        this(name, durable, false, false, null);
    }

    /**
     * Construct a new queue, given a name, durability, exclusive and auto-delete flags.
     *
     * @param name       the name of the queue.
     * @param durable    true if we are declaring a durable queue (the queue will survive a server restart)
     * @param exclusive  true if we are declaring an exclusive queue (the queue will only be used by the declarer's
     *                   connection)
     * @param autoDelete true if the server should delete the queue when it is no longer in use
     */
    public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete) {
        this(name, durable, exclusive, autoDelete, null);
    }

    /**
     * Construct a new queue, given a name, durability flag, and auto-delete flag, and arguments.
     *
     * @param name       the name of the queue - must not be null; set to "" to have the broker generate the name.
     * @param durable    true if we are declaring a durable queue (the queue will survive a server restart)
     * @param exclusive  true if we are declaring an exclusive queue (the queue will only be used by the declarer's
     *                   connection)
     * @param autoDelete true if the server should delete the queue when it is no longer in use
     * @param arguments  the arguments used to declare the queue
     */
    public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {

        super(arguments);

        Objects.requireNonNull(name, "'name' cannot be null");

        this.name = name;
        //this.actualName = StringUtils.hasText(name) ? name : (Base64UrlNamingStrategy.DEFAULT.generateName() + "_awaiting_declaration");
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
    }

    /**
     * Return the name provided in the constructor.
     *
     * @return the name.
     * @see #getActualName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * A durable queue will survive a server restart.
     *
     * @return true if durable.
     */
    public boolean isDurable() {
        return this.durable;
    }

    /**
     * True if the server should only send messages to the declarer's connection.
     *
     * @return true if exclusive.
     */
    public boolean isExclusive() {
        return this.exclusive;
    }

    /**
     * True if the server should delete the queue when it is no longer in use (the last consumer is cancelled). A queue
     * that never has any consumers will not be deleted automatically.
     *
     * @return true if auto-delete.
     */
    public boolean isAutoDelete() {
        return this.autoDelete;
    }


    @Override
    public String toString() {
        return "Queue [name=" + this.name
                + ", durable=" + this.durable
                + ", autoDelete=" + this.autoDelete
                + ", exclusive=" + this.exclusive
                + ", arguments=" + getArguments()
                + "]";
    }
}
