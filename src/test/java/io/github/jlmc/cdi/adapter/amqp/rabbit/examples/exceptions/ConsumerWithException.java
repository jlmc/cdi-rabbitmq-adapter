package io.github.jlmc.cdi.adapter.amqp.rabbit.examples.exceptions;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class ConsumerWithException {

    public static final String Q_EXAMPLE_INFO = "q.exampleEx.INFO";

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("Start:" + System.currentTimeMillis());

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");


        try (Connection connection = factory.newConnection(executorService);
             Channel channel1 = connection.createChannel();
             //Channel channel2 = connection.createChannel(2);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {

            //channel1.basicQos(1, false); // Per consumer limit
            //channel1.basicQos(1, true);  // Per channel limit

            Consumer consumer1 = new MessageConsumer(channel1, "Consumer-1");

            channel1.basicConsume(Q_EXAMPLE_INFO, false, consumer1);

            System.out.println("Write some thing to exit!!");
            bufferedReader.readLine();
        }

        System.out.println("----> EXIT");
    }

    /**
     * Consumers are expected to handle any exceptions that arise during handling of deliveries or any other consumer operations. Such exceptions should be logged, collected and ignored.
     * <p>
     * If a consumer cannot process deliveries due to a dependency not being available or similar reasons it should clearly log so and cancel itself until it is capable of processing deliveries again. This will make the consumer's unavailability visible to RabbitMQ and monitoring systems.
     */
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
            System.out.println("--- Consuming ... " + Instant.now());
            System.out.println(Thread.currentThread().getName() + " [%s] - ###[%s , %s]### ---> %s".formatted(name, routingKey, contentType, message));

            System.out.println("ENVELOPE: " + envelope);
            System.out.println("AMQP Properties: " + properties);

            simulateDelay();

            Channel channel = super.getChannel();

            System.out.println("--- Consumed ... " + Instant.now());

            // *** Result of the processing ***
            // Successful processing
            channel.basicAck(deliveryTag, false);


            // UnSuccessful processing
            //boolean requeue = true;
            //channel.basicNack(deliveryTag, false, requeue); // support multiple
            //channel.basicReject(deliveryTag, requeue); // support only
        }


        private void simulateDelay() {
            try {
                Thread.sleep(2_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
