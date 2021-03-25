package io.github.jlmc.cdi.adapter.amqp.rabbit.events.json;

import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;
import io.github.jlmc.cdi.adapter.amqp.rabbit.annotations.EventMediaType;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.Serializable;

/**
 * <h2>Performance Tip</h2>
 *
 * The Java documentation advised that for optimal use, instances of JsonbBuilder and Jsonb
 * should be reused and that for a typical use-case, only one Jsonb instance is required per application.
 * This is possible because its methods are thread safe.
 */
@ApplicationScoped
public class JsonbExposer implements Serializable {

    @Produces
    @ApplicationScoped
    @EventMediaType(contentType = ContentTypes.APPLICATION_JSON)
    public Jsonb expose() {
        return JsonbBuilder.create();
    }
}
