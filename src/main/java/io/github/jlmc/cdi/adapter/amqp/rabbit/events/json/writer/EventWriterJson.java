package io.github.jlmc.cdi.adapter.amqp.rabbit.events.json.writer;

import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventWriter;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
@EventMediaType(contentType = ContentTypes.APPLICATION_JSON)
public class EventWriterJson implements EventWriter {

    @Inject
    @EventMediaType(contentType = ContentTypes.APPLICATION_JSON)
    Jsonb jsonb;

    @Override
    public <T> byte[] write(T event, Class<?> type) {
        return jsonb.toJson(event, type).getBytes(StandardCharsets.UTF_8);
    }
}
