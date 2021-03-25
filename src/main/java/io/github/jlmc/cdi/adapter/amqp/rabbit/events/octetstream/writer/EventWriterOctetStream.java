package io.github.jlmc.cdi.adapter.amqp.rabbit.events.octetstream.writer;

import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventWriter;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

@ApplicationScoped
@EventMediaType(contentType = ContentTypes.APPLICATION_OCTET_STREAM)
public class EventWriterOctetStream implements EventWriter {

    @Override
    public <T> byte[] write(T event, Class<?> type) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {

            out.writeObject(event);
            out.flush();

            return bos.toByteArray();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
