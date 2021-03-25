package io.github.jlmc.cdi.adapter.amqp.rabbit.events.octetstream.reader;

import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.EventReader;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;

@ApplicationScoped
@EventMediaType(contentType = ContentTypes.APPLICATION_OCTET_STREAM)
public class EventReaderOctetStream implements EventReader {

    @Override
    public <T> T read(byte[] bytes, Class<T> type) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput objectInput = new ObjectInputStream(bis)) {

            Object object = objectInput.readObject();
            return type.cast(object);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
