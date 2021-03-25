package io.github.jlmc.cdi.adapter.amqp.rabbit.examples.exceptions;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class SimpleProducerForException {

    private static final String X_EXAMPLE = "x.exampleEx";
    private static final String X_EXAMPLE_DLX = "x.exampleEx.dlx";
    public static final String Q_EXAMPLE_INFO = "q.exampleEx.INFO";
    public static final String Q_EXAMPLE_INFO_DEAD = "q.exampleEx.INFO.DEAD";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");

        factory.setAutomaticRecoveryEnabled(true); // connection that will recover automatically
        factory.setConnectionTimeout(1_000);
        factory.setNetworkRecoveryInterval(10_000); // attempt recovery every 10 seconds
        factory.setTopologyRecoveryEnabled(true); // Topology recovery involves recovery of exchanges, queues, bindings and consumers. It is enabled by default but can be disabled:
        // Detecting Dead TCP Connections with Heartbeats and TCP Keepalives
        // https://www.rabbitmq.com/heartbeats.html
        factory.setRequestedHeartbeat(60); // // set the heartbeat timeout to 60 seconds


        /*
         .setSecure(true) // (4)
        .setConnectTimeout(10000) // (5)
        .setConnectRetryWaitTime(10000) // (6)
        .setRequestedConnectionHeartbeatTimeout(3) // (7)
        .withPrefetchCount(5); //8
         */

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
             Scanner scanner = new Scanner(System.in)) {

            channel.addShutdownListener(SimpleProducerForException::onChannelShutdown);

            // Publisher confirms are a RabbitMQ extension to the AMQP 0.9.1 protocol, so they are not enabled by default. Publisher confirms are enabled at the channel level with the confirmSelect method
            //channel.confirmSelect();

            //*******
            createResources(channel);

            for (int i = 1; true; ) {
                String text = scanner.nextLine();

                if ("q".equalsIgnoreCase(text)) {
                    break;
                }

                channel.txSelect();

                IntStream.range(1, 3)
                         .mapToObj(index -> "Message [" + index + "] -> " + text)
                         .map(str -> str.getBytes(StandardCharsets.UTF_8))
                         .forEach(bytes -> {


                             try {
                                 // TTL
                                 // https://www.rabbitmq.com/ttl.html
                                 AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                                         .expiration("60000")
                                         .contentType("application/text")
                                         .build();

                                 channel.basicPublish(X_EXAMPLE, "INFO", properties, bytes);

                                 //
                                 /*if (new String(bytes).contains("2")) {
                                     throw new IllegalStateException("sss");
                                 }*/

                                 System.out.printf("##--## Sent -> %s \n", new String(bytes));

                             } catch (Exception e) {
                                 e.printStackTrace();
                             }


                         });

                channel.txCommit();

            }
        }
    }

    private static void createResources(Channel channel) throws IOException {
        // create resource
        channel.exchangeDeclare(X_EXAMPLE, BuiltinExchangeType.DIRECT, true, false, null);
        channel.exchangeDeclare(X_EXAMPLE_DLX, BuiltinExchangeType.FANOUT, true, false, null);

        //channel.queueDeclare(Q_EXAMPLE_ERROR, false, false, true, null);
        channel.queueDeclare(Q_EXAMPLE_INFO_DEAD, true, false, false, null);

        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", X_EXAMPLE_DLX);
        args.put("x-dead-letter-routing-key", "");
        channel.queueDeclare(Q_EXAMPLE_INFO, true, false, false, args);

        //channel.queueBind(Q_EXAMPLE_ERROR, X_EXAMPLE, "ERROR");
        channel.queueBind(Q_EXAMPLE_INFO, X_EXAMPLE, "INFO");
        channel.queueBind(Q_EXAMPLE_INFO_DEAD, X_EXAMPLE_DLX, "");
    }

    private static void onChannelShutdown(ShutdownSignalException e) {
        System.out.println("Chanel ShutdownSignalException -> " + e.getMessage());
    }
}
