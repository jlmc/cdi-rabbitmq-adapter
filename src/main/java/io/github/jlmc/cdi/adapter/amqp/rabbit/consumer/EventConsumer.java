package io.github.jlmc.cdi.adapter.amqp.rabbit.consumer;

import io.github.jlmc.cdi.adapter.amqp.rabbit.EventReader;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Message;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import java.util.function.Function;

/**
 * Consumes AMQP messages and fires CDI events for these messages.
 */
public class EventConsumer extends AbstractMessageConsumer {

    private Event<Object> eventControl;
    private Instance<Object> eventPool;

    private Function<String, EventReader> eventReaderResolve;

    public EventConsumer(Event<Object> eventControl,
                         Instance<Object> eventPool,
                         Function<String, EventReader> eventReaderResolve) {
        this.eventControl = eventControl;
        this.eventPool = eventPool;
        this.eventReaderResolve = eventReaderResolve;
    }

    @Override
    public void handleMessage(Message message) {
        Object event = buildEvent(message);

        if (event != null) {
            eventControl.fire(event);
        }
    }

    private Object buildEvent(Message message) {
        //Object event = eventPool.get();
        if (message == null || message.getBodyContent() == null) {
            return null;
        }

        ConsumerConfiguration configuration = getConfiguration();
        String contentType = message.getContentType();

        EventReader eventReader = findReader(contentType);

        return eventReader.read(message.getBodyContent(), configuration.getEventType());
    }

    private EventReader findReader(String contentType) {
        return eventReaderResolve.apply(contentType);
    }
}
