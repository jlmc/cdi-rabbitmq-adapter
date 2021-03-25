package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.publisher.DeliveryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(Message.class);

    public static final int DELIVERY_MODE_PERSISTENT = 2;

    public static final Charset DEFAULT_MESSAGE_CHARSET = StandardCharsets.UTF_8;

    private byte[] bodyContent = new byte[0];
    private AMQP.BasicProperties basicProperties;
    private String routingKey = "";
    private String exchange = "";
    private long deliveryTag;

    public Message() {
        this(MessageProperties.BASIC.builder()
                                    .contentEncoding(DEFAULT_MESSAGE_CHARSET.name())
                                    .contentType(ContentTypes.APPLICATION_JSON)
                                    .build());
    }

    public Message(AMQP.BasicProperties basicProperties) {
        this.basicProperties = basicProperties;
    }

    /**
     * Publishes a message via the given channel.
     *
     * @param channel The channel used to publish the message
     */
    public void publish(Channel channel) throws IOException{
        publish(channel, DeliveryOptions.NONE);
    }

    public void publish(Channel channel, DeliveryOptions deliveryOptions) throws IOException {
        if (basicProperties.getTimestamp() == null) {
            basicProperties.builder().timestamp(new Date());
        }

        boolean mandatory = deliveryOptions == DeliveryOptions.MANDATORY;
        boolean immediate = deliveryOptions == DeliveryOptions.IMMEDIATE;

        LOGGER.info("Publishing message to exchange '{}' with routing key '{}' (deliveryOptions: {}, persistent: {})",
                exchange, routingKey, deliveryOptions, basicProperties.getDeliveryMode() == 2);

        channel.basicPublish(exchange, routingKey, mandatory, immediate, basicProperties, bodyContent);

        LOGGER.info("Successfully published message to exchange '{}' with routing key '{}'", exchange, routingKey);
    }

    public Message routingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public Message exchange(String exchange) {
        this.exchange = exchange;
        return this;
    }

    /**
     * <p>
     * Flags the message to be a persistent message. A persistent message survives a total broker failure as it is
     * persisted to disc if not already delivered to and acknowledged by all consumers.
     * </p>
     *
     * <p>
     * Important: This flag only has affect if the queues on the broker are flagged as durable.
     * </p>
     *
     * @return The modified message
     */
    public Message persistent() {
        basicProperties = basicProperties.builder()
                                         .deliveryMode(DELIVERY_MODE_PERSISTENT)
                                         .build();
        return this;
    }

    /**
     * <p>
     * Sets the delivery tag for this message.
     * </p>
     *
     * <p>
     * Note: The delivery tag is generated automatically if not set manually.
     * </p>
     *
     * @param deliveryTag The delivery tag
     * @return The modified message
     */
    public Message deliveryTag(long deliveryTag) {
        this.deliveryTag = deliveryTag;
        return this;
    }

    /**
     * Adds the given body content to the message.
     *
     * @param bodyContent The body content as bytes
     * @return The modified message
     */
    public Message body(byte[] bodyContent) {
        this.bodyContent = bodyContent;
        return this;
    }

    /**
     * Sets the content charset encoding of this message.
     *
     * @param charset The charset encoding
     * @return The modified message
     */
    public Message contentEncoding(String charset) {
        basicProperties = basicProperties.builder()
                                         .contentEncoding(charset)
                                         .build();
        return this;
    }

    /**
     * Sets the content type of this message.
     *
     * @param contentType The content type
     * @return The modified message
     */
    public Message contentType(String contentType) {
        basicProperties = basicProperties.builder()
                                         .contentType(contentType)
                                         .build();
        return this;
    }

    public String getContentType() {
        String contentType = basicProperties.getContentType();

        if (contentType == null) {
            return ContentTypes.APPLICATION_OCTET_STREAM;
        }

        return contentType;
    }

    public long getDeliveryTag() {
        return deliveryTag;
    }

    public byte[] getBodyContent() {
        return bodyContent;
    }
}
