package io.github.jlmc.cdi.adapter.amqp.rabbit.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Declarables {

    private final Collection<Declarable> declarables = new ArrayList<>();

    public Declarables(Declarable... declarables) {
        if (declarables != null && declarables.length > 0) {
            this.declarables.addAll(Arrays.asList(declarables));
        }
    }

    public Declarables(Collection<Declarable> declarables) {
        Objects.requireNonNull(declarables, "declarables cannot be null");
        this.declarables.addAll(declarables);
    }

    public Collection<Declarable> getDeclarables() {
        return this.declarables;
    }

    public boolean isEmpty() {
        return this.declarables.isEmpty();
    }

    /**
     * Return the elements that are instances of the provided class.
     * @param <T> The type.
     * @param type the type's class.
     * @return the filtered list.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getDeclarablesByType(Class<T> type) {
        return this.declarables.stream()
                               .filter(type::isInstance)
                               .map(dec -> (T) dec)
                               .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Declarables [declarables=" + this.declarables + "]";
    }

}
