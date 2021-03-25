package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

/**
 * Classes implementing this interface can be auto-declared with the broker during context initialization by an AmqpAdmin.
 * Registration can be limited to specific AmqpAdmins.
 */
public interface Declarable {

    /**
     * Add an argument to the declarable.
     * @param name the argument name.
     * @param value the argument value.
     * @since 2.2.2
     */
    default void addArgument(String name, Object value) {
        // default no-op
    }

    /**
     * Remove an argument from the declarable.
     * @param name the argument name.
     * @return the argument value or null if not present.
     * @since 2.2.2
     */
    default Object removeArgument(String name) {
        return null;
    }
}
