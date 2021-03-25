package io.github.jlmc.cdi.adapter.amqp.rabbit.events.text.writer;

import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventWriter;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;
import io.github.jlmc.cdi.adapter.amqp.rabbit.events.text.TextPlainConverter;
import io.github.jlmc.cdi.adapter.amqp.rabbit.events.text.TextPlanConverters;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
@EventMediaType(contentType = ContentTypes.TEXT_PLAIN)
public class EventWriterTextPlain extends TextPlanConverters implements EventWriter {

    @Inject
    TextPlanConverters textPlanConverters;

    @Override
    @SuppressWarnings("unchecked")
    public <T> byte[] write(T event, Class<?> type) {
        TextPlainConverter<T> converter = (TextPlainConverter<T>) textPlanConverters.findOrThrow(type);

        String text = converter.toText(event);

        return text.getBytes(StandardCharsets.UTF_8);
    }
}
