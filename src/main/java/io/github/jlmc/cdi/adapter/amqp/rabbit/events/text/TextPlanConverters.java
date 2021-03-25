package io.github.jlmc.cdi.adapter.amqp.rabbit.events.text;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TextPlanConverters implements Serializable {

    @Any
    @Inject
    Instance<TextPlainConvertersConfigurator> convertersConfigurators;

    protected Map<Class<?>, TextPlainConverter<?>> converterMapHolder = Collections.emptyMap();

    @PostConstruct
    public void initialize() {
        Map<Class<?>, TextPlainConverter<?>> accumulator = new HashMap<>();
        for (TextPlainConvertersConfigurator convertersConfigurator : convertersConfigurators) {
            Map<Class<?>, TextPlainConverter<?>> configure = convertersConfigurator.configure();

            accumulator.putAll(configure);
        }

        this.converterMapHolder = Map.copyOf(accumulator);
    }

    @SuppressWarnings("unchecked")
    public <T> TextPlainConverter<T> find(Class<T> type) {
        return (TextPlainConverter<T>) this.converterMapHolder.get(type);
    }

    public <T> TextPlainConverter<T> findOrThrow(Class<T> type) {
        TextPlainConverter<T> converter = find(type);

        if (converter == null) {
            throw new IllegalStateException("No TextPlainConverter implementation for the type <%s>".formatted(type));
        }

        return converter;
    }
}
