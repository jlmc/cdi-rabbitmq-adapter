package io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se;

import io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.events.recived.DeliveryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ExampleEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleEventConsumer.class);

    public void onDeliveryEvent(@Observes DeliveryEvent event) {
        LOGGER.info("Business method handler ----> <{}>", event);

        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
