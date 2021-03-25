package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.LinkedHashMap;
import java.util.Map;

class AbstractDeclarable implements Declarable {

    private final Map<String, Object> arguments;

    public AbstractDeclarable(Map<String, Object> arguments) {
        if (arguments == null) {
            this.arguments = new LinkedHashMap<>();
        } else {
            this.arguments = new LinkedHashMap<>(arguments);
        }
    }

    @Override
    public void addArgument(String name, Object value) {
        this.arguments.put(name, value);
    }

    @Override
    public Object removeArgument(String name) {
        return this.arguments.remove(name);
    }

    public Map<String, Object> getArguments() {
        return Map.copyOf(this.arguments);
    }
}
