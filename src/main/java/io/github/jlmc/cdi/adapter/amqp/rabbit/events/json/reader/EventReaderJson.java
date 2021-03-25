package io.github.jlmc.cdi.adapter.amqp.rabbit.events.json.reader;


import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventReader;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import java.io.ByteArrayInputStream;

@ApplicationScoped
@EventMediaType(contentType = ContentTypes.APPLICATION_JSON)
public class EventReaderJson implements EventReader {

    @Inject
    @EventMediaType(contentType = ContentTypes.APPLICATION_JSON)
    Jsonb jsonb;

    @Override
    public <T> T read(byte[] bytes, Class<T> type) {
        return jsonb.fromJson(new ByteArrayInputStream(bytes), type);
    }
}
