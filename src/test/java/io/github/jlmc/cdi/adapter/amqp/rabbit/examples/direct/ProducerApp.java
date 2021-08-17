package io.github.jlmc.cdi.adapter.amqp.rabbit.examples.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

public class ProducerApp {

    private static final String TEMPLATE_MESSAGE = "[%s]-MSG-[%d]-[%s]";
    private static final String X_EXAMPLE = "x.example";
    public static final String Q_EXAMPLE_ERROR = "q.example.ERROR";
    public static final String Q_EXAMPLE_INFO = "q.example.INFO";
    private static String[] SEVERITIES = {"ERROR", "INFO", "WARNING"};

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

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

            channel.addShutdownListener(ProducerApp::onChannelShutdown);
            //*******
            //****
            //Publisher Confirms
            // https://www.rabbitmq.com/tutorials/tutorial-seven-java.html
            // Publisher confirms are a RabbitMQ extension to the AMQP 0.9.1 protocol, so they are not enabled by default. Publisher confirms are enabled at the channel level with the confirmSelect method
            channel.confirmSelect();

            //*******


            // create resource
            channel.exchangeDeclare(X_EXAMPLE, BuiltinExchangeType.DIRECT, false, false, null);

            channel.queueDeclare(Q_EXAMPLE_ERROR, false, false, true, null);
            channel.queueDeclare(Q_EXAMPLE_INFO, false, false, true, null);

            channel.queueBind(Q_EXAMPLE_ERROR, X_EXAMPLE, "ERROR");
            channel.queueBind(Q_EXAMPLE_INFO, X_EXAMPLE, "INFO");





            for (int i = 0; i < 2; i++) {

                for (String severity : SEVERITIES) {

                    byte[] message = String.format(TEMPLATE_MESSAGE, severity, i, Instant.now()).getBytes(StandardCharsets.UTF_8);
                    channel.basicPublish(X_EXAMPLE, severity, null, message);

                    System.out.printf("##--## Sent -> %s \n", new String(message));

                }

            }

        }
    }

    private static void onChannelShutdown(ShutdownSignalException e) {
        System.out.println("Chanel ShutdownSignalException -> " + e.getMessage());
    }
}
