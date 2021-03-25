package io.github.jlmc.cdi.adapter.amqp.rabbit.events;

import io.github.jlmc.cdi.adapter.amqp.rabbit.EventReader;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.Serializable;

@ApplicationScoped
public class EventReaderHolder implements Serializable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventReaderHolder.class);

    @Any
    @Inject
    Instance<EventReader> readers;

    public EventReader findReader(String contentType) {
        try {
            return readers.select(EventMediaType.EventMediaTypeLiteral.of(contentType)).get();
        } catch (Throwable t) {
            LOGGER.error("Can't find EventWriter implementation for the contentType <{}>", contentType);
            throw new IllegalStateException("Can't find EventReader implementation for the contentType <%s>".formatted(contentType), t);
        }
    }

}
