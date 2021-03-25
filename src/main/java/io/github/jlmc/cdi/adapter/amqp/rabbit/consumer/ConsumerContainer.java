package io.github.jlmc.cdi.adapter.amqp.rabbit.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionListener;
import io.github.jlmc.cdi.adapter.amqp.rabbit.internal.SingleConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * A consumer container hosts consumers and manages their lifecycle.
 */
public class ConsumerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerContainer.class);

    private ConnectionFactory connectionFactory;
    private List<ConsumerHolder> consumerHolders = Collections.synchronizedList(new LinkedList<>());

    private final Object activationMonitor = new Object();

    public ConsumerContainer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        if (connectionFactory instanceof SingleConnectionFactory) {
            ContainerConnectionListener connectionListener = new ContainerConnectionListener();
            ((SingleConnectionFactory) connectionFactory).registerListener(connectionListener);
        }
    }

    /**
     * Adds a consumer to the container and binds it to the given queue with auto acknowledge disabled.
     * Does NOT enable the consumer to consume from the message broker until the container is started.
     *
     * @param consumer The consumer
     * @param queue    The queue to bind the consume to
     */
    public void addConsumer(Consumer consumer, String queue) {
        addConsumer(consumer, new ConsumerConfiguration(queue));
    }

    /**
     * Adds a consumer to the container, binds it to the given queue and sets whether the consumer auto acknowledge
     * or not. Does NOT enable the consumer to consume from the message broker until the container is started.
     *
     * @param consumer The consumer
     * @param queue    The queue to bind the consume to
     * @param autoAck  whether the consumer shall auto ack or not
     */
    public void addConsumer(Consumer consumer, String queue, boolean autoAck) {
        addConsumer(consumer, new ConsumerConfiguration(queue, autoAck));
    }

    /**
     * <p>Adds a consumer to the container, binds it to the given queue with auto acknowledge disabled.
     * Does NOT enable the consumer to consume from the message broker until the container is started.</p>
     *
     * <p>Registers the same consumer N times at the queue according to the number of specified instances.
     * Use this for scaling your consumers locally. Be aware that the consumer implementation must
     * be stateless or thread safe.</p>
     *
     * @param consumer             The consumer
     * @param queue                The queue to bind the consume to
     * @param prefetchMessageCount The number of message the client keep in buffer before processing them.
     * @param instances            the amount of consumer instances
     */
    public void addConsumer(Consumer consumer, String queue, int prefetchMessageCount, int instances) {
        addConsumer(consumer, new ConsumerConfiguration(queue).consumerInstances(instances).prefetchMessageCount(prefetchMessageCount));
    }

    /**
     * <p>Adds a consumer to the container, binds it to the given queue with auto acknowledge disabled.
     * Does NOT enable the consumer to consume from the message broker until the container is started.</p>
     *
     * <p>Registers the same consumer N times at the queue according to the number of specified instances.
     * Use this for scaling your consumers locally. Be aware that the consumer implementation must
     * be stateless or thread safe.</p>
     *
     * @param consumer  The consumer
     * @param queue     The queue to bind the consume to
     * @param instances the amount of consumer instances
     */
    public void addConsumer(Consumer consumer, String queue, int instances) {
        addConsumer(consumer, new ConsumerConfiguration(queue).consumerInstances(instances));
    }

    /**
     * <p>Adds a consumer to the container, binds it to the given queue and sets whether the consumer auto acknowledge
     * or not. Does NOT enable the consumer to consume from the message broker until the container is started.</p>
     *
     * <p>Registers the same consumer N times at the queue according to the number of specified instances.
     * Use this for scaling your consumers locally. Be aware that the consumer implementation must
     * be stateless or thread safe.</p>
     *
     * @param consumer             The consumer
     * @param queue                The queue to bind the consume to
     * @param autoAck              whether the consumer shall auto ack or not
     * @param prefetchMessageCount The number of message the client keep in buffer before processing them.
     * @param instances            the amount of consumer instances
     */
    public void addConsumer(Consumer consumer, String queue, boolean autoAck, int prefetchMessageCount, int instances) {
        addConsumer(consumer, new ConsumerConfiguration(queue, autoAck).prefetchMessageCount(prefetchMessageCount).consumerInstances(instances));
    }

    /**
     * <p>Adds a consumer to the container, binds it to the given queue and sets whether the consumer auto acknowledge
     * or not. Does NOT enable the consumer to consume from the message broker until the container is started.</p>
     *
     * <p>Registers the same consumer N times at the queue according to the number of specified instances.
     * Use this for scaling your consumers locally. Be aware that the consumer implementation must
     * be stateless or thread safe.</p>
     *
     * @param consumer  The consumer
     * @param queue     The queue to bind the consume to
     * @param autoAck   whether the consumer shall auto ack or not
     * @param instances the amount of consumer instances
     */
    public void addConsumer(Consumer consumer, String queue, boolean autoAck, int instances) {
        addConsumer(consumer,
                new ConsumerConfiguration(queue, autoAck).consumerInstances(instances));
    }

    /**
     * Adds a consumer to the container and configures it according to the consumer
     * configuration. Does NOT enable the consumer to consume from the message broker until the container is started.
     *
     * <p>Registers the same consumer N times at the queue according to the number of specified instances.
     * Use this for scaling your consumers locally. Be aware that the consumer implementation must
     * be stateless or thread safe.</p>
     *
     * @param consumer      The consumer
     * @param configuration The consumer configuration
     */
    public synchronized void addConsumer(Consumer consumer, ConsumerConfiguration configuration) {
        for (int i = 0; i < configuration.getConsumerInstances(); i++) {
            this.consumerHolders.add(new ConsumerHolder(consumer, configuration, this::createChannel));
        }
    }

    private Channel createChannel() {
        try {
            LOGGER.debug("Creating channel");
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            LOGGER.debug("Created channel");
            return channel;
        } catch (IOException | TimeoutException e) {
            throw new IllegalStateException(e);
        }
    }

    public void startAllConsumers() {
        try {
            enableConsumers(consumerHolders);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void enableConsumers(List<ConsumerHolder> consumerHolders) throws IOException, TimeoutException {
        checkPreconditions(consumerHolders);

        try {
            for (ConsumerHolder consumerHolder : consumerHolders) {
                consumerHolder.enable();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to enable consumers - disabling already enabled consumers");
            disableConsumers(consumerHolders);
            throw e;
        }

    }

    /**
     * Disables all consumers in the given list after deactivating them.
     *
     * @param consumerHolders The consumers to disable
     */
    protected void disableConsumers(List<ConsumerHolder> consumerHolders) {
        for (ConsumerHolder consumerHolder : consumerHolders) {
            consumerHolder.disable();
        }
    }

    /**
     * Checks if all preconditions are fulfilled on the broker to
     * successfully register a consumer there. One important precondition
     * is the existence of the queue the consumer shall consume from.
     *
     * @param consumerHolders The consumer holders
     * @throws IOException if the precondition check fails
     */
    protected void checkPreconditions(List<ConsumerHolder> consumerHolders) throws IOException, TimeoutException {
        Channel channel = createChannel();

        for (ConsumerHolder consumerHolder : consumerHolders) {
            String queue = consumerHolder.getConfiguration().getQueueName();
            try {
                channel.queueDeclarePassive(queue);
                LOGGER.debug("Queue {} found on broker", queue);
            } catch (IOException e) {
                LOGGER.error("Queue {} not found on broker", queue);
                throw e;
            }
        }

        channel.close();
    }

    /**
     * Activates all consumers in the given list.
     *
     * @param consumerHolders The list of consumers to activate
     * @throws IOException if the activation process fails for a consumer
     */
    protected void activateConsumers(List<ConsumerHolder> consumerHolders) throws IOException,TimeoutException {
        synchronized (activationMonitor) {
            for (ConsumerHolder consumerHolder : consumerHolders) {
                try {
                    consumerHolder.activate();
                } catch (IOException e) {
                    LOGGER.error("Failed to activate consumer - deactivating already activated consumers");
                    deactivateConsumers(consumerHolders);
                    throw e;
                }
            }
        }
    }

    /**
     * Deactivates all consumers in the given list.
     *
     * @param consumerHolders The list of consumers to deactivate.
     */
    protected void deactivateConsumers(List<ConsumerHolder> consumerHolders) {
        synchronized (activationMonitor) {
            for (ConsumerHolder consumerHolder : consumerHolders) {
                consumerHolder.deactivate();
            }
        }
    }

    /**
     * Filters the consumers matching the given enabled flag from the list of managed consumers.
     *
     * @param enabled Whether to filter for enabled or disabled consumers
     * @return The filtered consumers
     */
    protected List<ConsumerHolder> filterConsumersForEnabledFlag(boolean enabled) {
        List<ConsumerHolder> consumerHolderSubList = new LinkedList<>();
        for (ConsumerHolder consumerHolder : consumerHolders) {
            if (consumerHolder.isEnabled() == enabled) {
                consumerHolderSubList.add(consumerHolder);
            }
        }
        return consumerHolderSubList;
    }


    private static class ConsumerHolder {
        private final Supplier<Channel> channelSupplier;
        boolean enabled = false;
        boolean active = false;

        Channel channel;
        Consumer consumer;
        ConsumerConfiguration configuration;

        public ConsumerHolder(Consumer consumer, ConsumerConfiguration configuration,  Supplier<Channel> channelSupplier) {
            this.consumer = consumer;
            this.configuration = configuration;
            this.channelSupplier = channelSupplier;

            if (consumer instanceof ManagedConsumer) {
                ((ManagedConsumer) consumer).setConfiguration(configuration);
            }
        }

        public void enable() throws IOException {
            enabled = true;
            activate();
        }

        public void disable() {
            enabled = false;
            deactivate();
        }

        public ConsumerConfiguration getConfiguration() {
            return configuration;
        }

        private void activate() throws IOException {
            LOGGER.info("Activating consumer of class {}", consumer.getClass());
            // Make sure the consumer is not active before starting it
            if (isActive()) {
                deactivate();
            }
            // Start the consumer
            try {
                channel = channelSupplier.get();

                if (consumer instanceof ManagedConsumer) {
                    ((ManagedConsumer) consumer).setChannel(channel);
                }

                channel.basicConsume(configuration.getQueueName(), configuration.isAutoAck(), consumer);

                channel.basicQos(configuration.getPrefetchMessageCount());

                active = true;
                LOGGER.info("Activated consumer of class {}", consumer.getClass());

            } catch (IOException e) {
                LOGGER.error("Failed to activate consumer of class {}", consumer.getClass(), e);
                throw e;
            }
        }


        private boolean isActive() {
            return active;
        }

        public boolean isEnabled() {
            return enabled;
        }

        private void deactivate() {
            LOGGER.info("Deactivating consumer of class {}", consumer.getClass());

            if (channel != null) {
                try {
                    LOGGER.info("Closing channel for consumer of class {}", consumer.getClass());

                    channel.close();

                    LOGGER.info("Closed channel for consumer of class {}", consumer.getClass());
                } catch (Exception e) {
                    LOGGER.info("Aborted closing channel for consumer of class {} (already closing)", consumer.getClass());
                    // Ignore exception: In this case the channel is for sure not usable any more
                }
                channel = null;
            }
            active = false;
            LOGGER.info("Deactivated consumer of class {}", consumer.getClass());
        }
    }

    /**
     * A container connection listener to react on events of
     * a {@link SingleConnectionFactory} if used.
     *
     * @author christian.bick
     *
     */
    private class ContainerConnectionListener implements ConnectionListener {

        public void onConnectionEstablished(Connection connection) {
            String hostName = connection.getAddress().getHostName();
            LOGGER.info("Connection established to {}", hostName);
            List<ConsumerHolder> enabledConsumerHolders = filterConsumersForEnabledFlag(true);
            LOGGER.info("Activating {} enabled consumers", enabledConsumerHolders.size());
            try {
                activateConsumers(enabledConsumerHolders);
                LOGGER.info("Activated enabled consumers");
            } catch (IOException e) {
                LOGGER.error("Failed to activate enabled consumers", e);
                deactivateConsumers(enabledConsumerHolders);
            }catch(TimeoutException te){
                LOGGER.error("Failed to activate enabled consumers", te);
                deactivateConsumers(enabledConsumerHolders);
            }
        }

        public void onConnectionLost(Connection connection) {
            LOGGER.warn("Connection lost");
            LOGGER.info("Deactivating enabled consumers");
            List<ConsumerHolder> enabledConsumerHolders = filterConsumersForEnabledFlag(true);
            deactivateConsumers(enabledConsumerHolders);
        }

        public void onConnectionClosed(Connection connection) {
            LOGGER.warn("Connection closed for ever");
            LOGGER.info("Deactivating enabled consumers");
            List<ConsumerHolder> enabledConsumerHolders = filterConsumersForEnabledFlag(true);
            deactivateConsumers(enabledConsumerHolders);
        }
    }
}
