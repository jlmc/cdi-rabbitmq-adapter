package io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se;

import io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.events.sent.SentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@ApplicationScoped
public class ExampleEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleEventProducer.class);

    @Inject
    Event<SentEvent> eventBus;
    private ScheduledExecutorService executor;
    private AtomicLong count;

    //@PostConstruct
    public void initialize() {
        this.executor = Executors.newScheduledThreadPool(2);
        this.count = new AtomicLong(0);
        executor.scheduleAtFixedRate(this::produceEvents, 3, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        System.out.println("STOPPING");

        this.executor.shutdown();
    }

    private void produceEvents() {
        LOGGER.info("Producing some Examples events!!");

        final Instant now = Instant.now();

        IntStream.rangeClosed(1, 20)
                 .mapToObj(index -> "My Custom business event [%s] produced at [%s]".formatted(count.incrementAndGet(), now))
                 .map(SentEvent::of)
                 .forEach(eventBus::fire);
    }

    public void shutdown(@Observes @Initialized(ApplicationScoped.class) Object doesntMatter) {
       initialize();
    }


}
