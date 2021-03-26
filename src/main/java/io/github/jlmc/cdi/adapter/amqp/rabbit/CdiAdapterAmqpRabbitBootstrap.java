package io.github.jlmc.cdi.adapter.amqp.rabbit;


import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Declarable;
import io.github.jlmc.cdi.adapter.amqp.rabbit.core.Declarables;
import io.github.jlmc.cdi.adapter.amqp.rabbit.internal.DeclarablesAdministratorProcessor;
import io.github.jlmc.cdi.adapter.amqp.rabbit.internal.EventBinder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;

@ApplicationScoped
public class CdiAdapterAmqpRabbitBootstrap implements Serializable {

    @Any
    @Inject
    Instance<DeclarablesConfigurator> declarablesConfigurators;

    @Inject
    DeclarablesAdministratorProcessor declarablesAdministratorProcessor;

    @Inject
    EventBinder eventBinder;

    public void startup(@Observes @Initialized(ApplicationScoped.class) Object doesntMatter) {
        defineAllResources();

        initializeEventBinders();
    }

    public void shutdown(@Observes @Destroyed(ApplicationScoped.class) Object doesntMatter) {
    }

    private void initializeEventBinders() {
        eventBinder.initialize();
    }

    private void defineAllResources() {
        Declarables declarables =
                declarablesConfigurators.stream()
                                        .map(DeclarablesConfigurator::configure)
                                        .reduce(new Declarables(), (a, b) -> {

                                            ArrayList<Declarable> list = new ArrayList<>(a.getDeclarables());
                                            list.addAll(b.getDeclarables());

                                            return new Declarables(list);

                                        });

        if (!declarables.isEmpty()){
            declarablesAdministratorProcessor.create(declarables);
        }
    }

}
