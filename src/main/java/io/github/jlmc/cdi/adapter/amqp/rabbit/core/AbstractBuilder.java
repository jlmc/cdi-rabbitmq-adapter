package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class for builders supporting arguments.
 */
class AbstractBuilder {

    private Map<String, Object> arguments;

    /**
     * Return the arguments map, after creating one if necessary.
     * @return the arguments.
     */
    protected Map<String, Object> getOrCreateArguments() {
        if (this.arguments == null) {
            this.arguments = new LinkedHashMap<>();
        }
        return this.arguments;
    }

    protected Map<String, Object> getArguments() {
        return this.arguments;
    }
}
