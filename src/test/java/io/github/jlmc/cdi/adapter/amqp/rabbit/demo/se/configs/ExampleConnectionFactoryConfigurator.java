package io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.configs;

import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionFactoryConfigurations;
import io.github.jlmc.cdi.adapter.amqp.rabbit.ConnectionFactoryConfigurationsConfigurator;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class ExampleConnectionFactoryConfigurator implements ConnectionFactoryConfigurationsConfigurator {

    AtomicLong count = new AtomicLong(1);
    AtomicLong countScheduled = new AtomicLong(1);

    @Override
    public ConnectionFactoryConfigurations configure() {
        ExecutorService executorService = getExecutorService();
        ScheduledExecutorService scheduledExecutorService = getScheduledExecutorService();

        return ConnectionFactoryConfigurations.defaults()
                                              .setUsername("admin")
                                              .setPassword("admin")
                                              .setExecutorService(executorService)
                                              .setHeartbeatExecutor(scheduledExecutorService);
    }

    private ScheduledExecutorService getScheduledExecutorService() {
        return Executors.newScheduledThreadPool(2, getThreadFactory("example-scheduled-thread-%d", countScheduled));
    }

    private ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(10, getThreadFactory("example-thread-%d", count));
    }

    private ThreadFactory getThreadFactory(String s, AtomicLong count) {
        return r -> {
            ThreadFactory threadFactory = Executors.defaultThreadFactory();
            Thread thread = threadFactory.newThread(r);
            String name = String.format(s, count.getAndIncrement());
            thread.setName(name);
            return thread;
        };
    }
}
