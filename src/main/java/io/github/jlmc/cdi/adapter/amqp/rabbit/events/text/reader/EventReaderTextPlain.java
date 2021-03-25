package io.github.jlmc.cdi.adapter.amqp.rabbit.events.text.reader;

import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventReader;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;
import io.github.jlmc.cdi.adapter.amqp.rabbit.events.text.TextPlainConverter;
import io.github.jlmc.cdi.adapter.amqp.rabbit.events.text.TextPlanConverters;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
@EventMediaType(contentType = ContentTypes.TEXT_PLAIN)
public class EventReaderTextPlain implements EventReader {

    @Inject
    TextPlanConverters textPlanConverters;

    @Override
    public <T> T read(byte[] bytes, Class<T> type) {
        TextPlainConverter<T> converter = textPlanConverters.findOrThrow(type);

        Object o = converter.fromText(new String(bytes, StandardCharsets.UTF_8));

        return type.cast(o);
    }
}
