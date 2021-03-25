package io.github.jlmc.cdi.adapter.amqp.rabbit.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractMessageConsumer extends ManagedConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageConsumer.class);

    @Override
    public void handleConsumeOk(String consumerTag) {
        LOGGER.debug("Consumer <{}>: Received consume OK", consumerTag);
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        LOGGER.debug("Consumer <{}>: Received cancel OK", consumerTag);
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        LOGGER.debug("Consumer <{}>: Received cancel", consumerTag);
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        LOGGER.debug("Consumer <{}>: Received shutdown signal: <{}>", consumerTag, sig.getMessage());
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        LOGGER.debug("Consumer <{}>: Received recover OK", consumerTag);
    }

    /**
     * <p>Handles a message delivery from the broker by converting the received
     * message parts to a {@link Message} which provides convenient access to@param consumerTag
     * the message parts and hands it over to the {@link #handleMessage(Message)}@param envelope
     * method.</p>
     */
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        LOGGER.debug("Consumer <{}>: Received handler delivery", consumerTag);

        Message message =
                new Message(properties)
                        .exchange(envelope.getExchange())
                        .routingKey(envelope.getRoutingKey())
                        .deliveryTag(envelope.getDeliveryTag())
                        .body(body);

        try {
            LOGGER.info("Consumer <{}>: Received message <{}>", consumerTag, envelope.getDeliveryTag());
            handleMessage(message);
        } catch (Throwable cause) {

            if (!isAutoAck()) {
                LOGGER.error("Consumer <{}>: Message <{}> could not be handled due to an exception during message processing", consumerTag, envelope.getDeliveryTag(), cause);

                getChannel().basicNack(envelope.getDeliveryTag(), false, false);

                LOGGER.warn("Consumer <{}>: Nacked message <{}>", consumerTag, envelope.getDeliveryTag(), cause);
            }

            return;
        }

        if (!isAutoAck()) {
            try {

                getChannel().basicAck(envelope.getDeliveryTag(), false);

                LOGGER.debug("Consumer <{}>: Acked message <{}>", consumerTag, envelope.getDeliveryTag() );

            } catch(IOException e) {

                LOGGER.error("Consumer {}: Message {} was processed but could not be acknowledged due to an exception when sending the acknowledgement",
                        consumerTag, envelope.getDeliveryTag(), e);
                throw e;
            }
        }
    }

    private boolean isAutoAck() {
        ConsumerConfiguration configuration = getConfiguration();
        if (configuration == null) return false;
        return configuration.isAutoAck();
    }

    /**
     * <p>Handles a message delivered by the broker to the consumer.</p>
     *
     * <p>This method is expected to be overridden by extending
     * sub classes which contain the actual consumer implementation
     * and process the message.</p>
     *
     * <p>IMPORTANT: In case the consumer is configured to acknowledge
     * messages manually the acknowledgment is handled by the super class.
     * It is sent automatically after this method returned without
     * throwing and exception. In case the method throws an exception,
     * a negative acknowledgment will be sent.
     * </p>
     *
     * @param message The delivered message
     */
    public abstract void handleMessage(Message message);
}
