package io.github.jlmc.cdi.adapter.amqp.rabbit.events;

import io.github.jlmc.cdi.adapter.amqp.rabbit.EventWriter;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.Serializable;

@ApplicationScoped
public class EventWriterHolder implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventWriterHolder.class);

    @Any
    @Inject
    Instance<EventWriter> writers;

    public EventWriter findWriter(String contentType) {
        try {
            return writers.select(EventMediaType.EventMediaTypeLiteral.of(contentType)).get();
        } catch (Throwable t) {
            LOGGER.error("Can't find EventWriter implementation for the contentType <{}>", contentType);
            throw new IllegalStateException(String.format("Can't find EventWriter implementation for the contentType <%s>", contentType), t);
        }
    }
}
