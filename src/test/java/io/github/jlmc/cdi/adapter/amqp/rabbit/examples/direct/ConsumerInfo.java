package io.github.jlmc.cdi.adapter.amqp.rabbit.examples.direct;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class ConsumerInfo {

    public static final String Q_EXAMPLE_ERROR = "q.example.ERROR";
    public static final String Q_EXAMPLE_INFO = "q.example.INFO";


    public static void main(String[] args) throws IOException, TimeoutException {


        ExecutorService executorService = Executors.newFixedThreadPool(4);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");


        try (Connection connection = factory.newConnection(executorService);
             Channel channel = connection.createChannel(2);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {

            channel.basicQos(1, false); // Per consumer limit
            channel.basicQos(2, true);  // Per channel limit

            Consumer consumerError = new MessageConsumer(channel, "ERROR");
            Consumer consumer2 = new MessageConsumer(channel, "INFO");

            channel.basicConsume(Q_EXAMPLE_ERROR, false, consumerError);
            channel.basicConsume(Q_EXAMPLE_INFO, false, consumer2);

            System.out.println("Write some thing to exit!!");
            bufferedReader.readLine();
        }

        System.out.println("----> EXIT");
    }

    static class MessageConsumer extends DefaultConsumer {

        private String name;

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public MessageConsumer(Channel channel, String name) {
            super(channel);
            this.name = name;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            //super.handleDelivery(consumerTag, envelope, properties, body);
            String message = new String(body, StandardCharsets.UTF_8);

            String routingKey = envelope.getRoutingKey();
            String contentType = properties.getContentType();
            long deliveryTag = envelope.getDeliveryTag();

            // (process the message components here ...)
            System.out.println(Thread.currentThread().getName() + " [%s] - ###[%s , %s]### ---> %s".formatted(name, routingKey, contentType, message));

            Channel channel = super.getChannel();
            channel.basicAck(deliveryTag, false);
        }
    }
}
