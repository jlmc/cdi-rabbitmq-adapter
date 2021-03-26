package io.github.jlmc.cdi.adapter.amqp.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class ConnectionFactoryConfigurations {

    private String host = ConnectionFactory.DEFAULT_HOST;
    private String username = ConnectionFactory.DEFAULT_USER;
    private String password = ConnectionFactory.DEFAULT_PASS;
    private String virtualHost = ConnectionFactory.DEFAULT_VHOST;
    private boolean useSslProtocol = false;
    private String sslProtocol = "TLSv1.2";
    private int port = AMQP.PROTOCOL.PORT;
    private boolean automaticRecoveryEnabled = true;
    private int connectionTimeout = 1_000;
    private long networkRecoveryInterval = 10_000L;
    private boolean topologyRecoveryEnabled = true;
    private int requestedHeartbeatInSec = 60;
    private ExecutorService executorService = null;
    private ScheduledExecutorService heartbeatExecutor = null;

    protected ConnectionFactoryConfigurations() {}

    public static ConnectionFactoryConfigurations defaults() {
        return new ConnectionFactoryConfigurations();
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public boolean isAutomaticRecoveryEnabled() {
        return this.automaticRecoveryEnabled;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public long getNetworkRecoveryInterval() {
        return this.networkRecoveryInterval;
    }

    public boolean isTopologyRecoveryEnabled() {
        return this.topologyRecoveryEnabled;
    }

    public int getRequestedHeartbeatInSec() {
        return this.requestedHeartbeatInSec;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ScheduledExecutorService getHeartbeatExecutor() {
        return this.heartbeatExecutor;
    }

    public ConnectionFactoryConfigurations setUsername(String username) {
        this.username = username;
        return this;
    }

    public ConnectionFactoryConfigurations setHost(String host) {
        this.host = host;
        return this;
    }

    public ConnectionFactoryConfigurations setPassword(String password) {
        this.password = password;
        return this;
    }

    public ConnectionFactoryConfigurations setAutomaticRecoveryEnabled(boolean automaticRecoveryEnabled) {
        this.automaticRecoveryEnabled = automaticRecoveryEnabled;
        return this;
    }

    public ConnectionFactoryConfigurations setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public ConnectionFactoryConfigurations setNetworkRecoveryInterval(long networkRecoveryInterval) {
        this.networkRecoveryInterval = networkRecoveryInterval;
        return this;
    }

    public ConnectionFactoryConfigurations setTopologyRecoveryEnabled(boolean topologyRecoveryEnabled) {
        this.topologyRecoveryEnabled = topologyRecoveryEnabled;
        return this;
    }

    public ConnectionFactoryConfigurations setHeartbeatExecutor(ScheduledExecutorService heartbeatExecutor) {
        this.heartbeatExecutor = heartbeatExecutor;
        return this;
    }

    public ConnectionFactoryConfigurations setRequestedHeartbeatInSec(int requestedHeartbeatInSec) {
        this.requestedHeartbeatInSec = requestedHeartbeatInSec;
        return this;
    }

    public ConnectionFactoryConfigurations setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public ConnectionFactoryConfigurations setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
        return this;
    }

    public ConnectionFactoryConfigurations setUseSslProtocol() {
        this.useSslProtocol = true;
        return this;
    }

    public boolean useSslProtocol() {
        return useSslProtocol;
    }

    public ConnectionFactoryConfigurations setSslProtocol(String protocol) {
        this.sslProtocol = protocol;
        return this;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public ConnectionFactoryConfigurations setPort(int port) {
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }
}
