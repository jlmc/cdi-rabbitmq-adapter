package io.github.jlmc.cdi.adapter.amqp.rabbit.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;


public abstract class ManagedConsumer implements Consumer {

    private Channel channel;
    private ConsumerConfiguration configuration;

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setConfiguration(ConsumerConfiguration configuration) {
        this.configuration = configuration;
    }

    public ConsumerConfiguration getConfiguration() {
        return configuration;
    }
}
