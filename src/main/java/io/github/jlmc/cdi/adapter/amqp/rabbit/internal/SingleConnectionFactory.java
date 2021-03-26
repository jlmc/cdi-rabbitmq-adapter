package io.github.jlmc.cdi.adapter.amqp.rabbit.internal;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionFactoryConfigurations;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>A single connection factory provides ONE SINGLE connection to a
 * RabbitMQ message broker via TCP.</p>
 *
 * <p>It is recommended by the RabbitMQ documentation (v2.7) to use
 * one single connection within a client and to use one channel for
 * every client thread.</p>
 */
public class SingleConnectionFactory extends ConnectionFactory {

    private static final long CONNECTION_ESTABLISH_INTERVAL_IN_MS = 500L;

    private enum State {
        /**
         * The factory has never established a connection so far.
         */
        NEVER_CONNECTED,
        /**
         * The factory has established a connection in the past but the connection was lost and the
         * factory is currently trying to reestablish the connection.
         */
        CONNECTING,
        /**
         * The factory has established a connection that is currently alive and that can be retrieved.
         */
        CONNECTED,
        /**
         * The factory and its underlying connection are closed and the factory cannot be used to retrieve
         * new connections.
         */
        CLOSED
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleConnectionFactory.class);

    private ExecutorService executorService;
    private final AtomicReference<State> state = new AtomicReference<>(State.NEVER_CONNECTED);
    private final AtomicReference<Connection> connection = new AtomicReference<>(null);
    private List<ConnectionListener> connectionListeners = new CopyOnWriteArrayList<>();
    private boolean tryToReconnectByItSelf = false;
    private final Object operationOnConnectionMonitor = new Object();

    public SingleConnectionFactory(ConnectionFactoryConfigurations configurations) {
        super();
        this.executorService = configurations.getExecutorService();

        setHost(configurations.getHost());
        setUsername(configurations.getUsername());
        setPassword(configurations.getPassword());
        setVirtualHost(configurations.getVirtualHost());
        setPort(configurations.getPort());

        if (configurations.useSslProtocol()) {
            useSslProtocol(getSslContext(configurations.getSslProtocol()));
        }

        setSharedExecutor(executorService);
        setShutdownExecutor(executorService);
        setTopologyRecoveryExecutor(executorService);
        setHeartbeatExecutor(configurations.getHeartbeatExecutor());

        // Automatic Recovery
        setAutomaticRecoveryEnabled(configurations.isAutomaticRecoveryEnabled()); // connection that will recover automatically
        setConnectionTimeout(configurations.getConnectionTimeout());
        setNetworkRecoveryInterval(configurations.getNetworkRecoveryInterval()); // attempt recovery every 10 seconds
        setTopologyRecoveryEnabled(configurations.isTopologyRecoveryEnabled()); // Topology recovery involves recovery of exchanges, queues, bindings and consumers. It is enabled by default but can be disabled:
        // Detecting Dead TCP Connections with Heartbeats and TCP Keepalives
        // https://www.rabbitmq.com/heartbeats.html
        setRequestedHeartbeat(configurations.getRequestedHeartbeatInSec()); //
        //connectionShutdownListener = new ConnectionShutDownListener();
    }

    private SSLContext getSslContext(String protocol) {
        try {
            return SSLContext.getInstance(protocol);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Gets a new connection from the factory.
     * <p>
     * As this factory only provides one connection for every process, the connection
     * is established on the first call of this method. Every subsequent call will return the same instance of the first
     * established connection.</p>
     *
     * <p>In case a connection is lost, the factory will try to reestablish
     * a new connection.</p>
     *
     * @return The connection
     */
    @Override
    public Connection newConnection() throws IOException, TimeoutException {
        if (State.CLOSED == this.state.get()) {
            throw new IOException("Attempt to retrieve a connection from a closed connection factory");
        }

        if (State.NEVER_CONNECTED == this.state.get()) {
            establishConnection();
        }

        // Retrieve the connection if it is established
        if (connection.get() != null && connection.get().isOpen()) {
            return connection.get();
        }

        // Throw an exception if no established connection could not be retrieved
        LOGGER.error("Unable to retrieve connection");
        throw new IOException("Unable to retrieve connection");
    }

    public void close() {
        synchronized (operationOnConnectionMonitor) {
            if (State.CLOSED == state.get()) {
                LOGGER.warn("Attempt to close connection factory which is already closed");
                return;
            }

            LOGGER.info("Closing connection factory");

            if (connection.get() != null) {
                try {
                    connection.get().close();
                    connection.set(null);
                } catch (IOException e) {

                    if (!connection.get().isOpen()) {
                        LOGGER.warn("Attempt to close an already closed connection");
                    } else {
                        LOGGER.error("Unable to close current connection", e);
                    }

                }
            }
            changeState(State.CLOSED);

            LOGGER.info("Closed connection factory");
        }
    }

    /**
     * Establishes a new connection.
     *
     * @throws IOException if establishing a new connection fails
     */
    private void establishConnection() throws IOException, TimeoutException {
        synchronized (operationOnConnectionMonitor) {

            if (State.CLOSED == state.get()) {
                throw new IOException("Attempt to establish a connection with a closed connection factory");
            } else if (State.CONNECTED == state.get()) {
                LOGGER.warn("Establishing new connection although a connection is already established");
            }

            try {
                LOGGER.info("Trying to establish connection to {}:{}", getHost(), getPort());

                connection.set(super.newConnection(executorService));

                connection.get().addShutdownListener(this::onConnectionShutdown);


                LOGGER.info("Established connection to {}:{}", getHost(), getPort());

                changeState(State.CONNECTED);

            } catch (IOException e) {
                LOGGER.error("Failed to establish connection to {}:{}", getHost(), getPort());
                throw e;
            }
        }
    }

    /**
     * A listener to register on the parent factory to be notified
     * about connection shutdowns.
     */
    private void onConnectionShutdown(ShutdownSignalException cause) {
        // Only hard error means loss of connection
        if (!cause.isHardError()) {
            return;
        }

        synchronized (operationOnConnectionMonitor) {
            // No action to be taken if factory is already closed or already connecting
            if (State.CLOSED == state.get() || State.CONNECTING == state.get()) {
                return;
            }

            changeState(State.CONNECTING);
        }

        LOGGER.error("Connection to {}:{} lost", getHost(), getPort());

        if (tryToReconnectByItSelf) {
            while (State.CONNECTING == state.get()) {
                try {
                    establishConnection();
                    return;
                } catch (IOException | TimeoutException e) {
                    LOGGER.info("Next reconnect attempt in {} ms", CONNECTION_ESTABLISH_INTERVAL_IN_MS);
                    try {
                        Thread.sleep(CONNECTION_ESTABLISH_INTERVAL_IN_MS);
                    } catch (InterruptedException ie) {
                        // that's fine, simply stop here
                        return;
                    }
                }
            }
        }
    }

    private void changeState(State state) {
        this.state.set(state);
        notifyListenersOnStateChange();
    }

    /**
     * Registers a connection listener at the factory which
     * is notified about changes of connection states.
     *
     * @param connectionListener The connection listener
     */
    public void registerListener(ConnectionListener connectionListener) {
        connectionListeners.add(connectionListener);
    }

    /**
     * Removes a connection listener from the factory.
     *
     * @param connectionListener The connection listener
     */
    public void removeConnectionListener(ConnectionListener connectionListener) {
        connectionListeners.remove(connectionListener);
    }

    /**
     * Notifies all connection listener about a state change.
     */
    void notifyListenersOnStateChange() {
        LOGGER.debug("Notifying connection listeners about state change to {}", state);

        for (ConnectionListener listener : connectionListeners) {
            switch (state.get()) {
                case CONNECTED:
                    listener.onConnectionEstablished(connection.get());
                    break;
                case CONNECTING:
                    listener.onConnectionLost(connection.get());
                    break;
                case CLOSED:
                    listener.onConnectionClosed(connection.get());
                    break;
                default:
                    break;
            }
        }
    }

}
