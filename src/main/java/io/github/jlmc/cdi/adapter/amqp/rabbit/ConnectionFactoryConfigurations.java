package io.github.jlmc.cdi.adapter.amqp.rabbit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class ConnectionFactoryConfigurations {

    private String host = "localhost";
    private String username = null;
    private String password = null;
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

}
