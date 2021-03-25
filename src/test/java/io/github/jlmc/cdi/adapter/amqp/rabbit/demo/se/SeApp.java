package io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se;

import io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.configs.ExampleBindingsConfigurator;
import io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.configs.ExampleConnectionFactoryConfigurator;
import io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.configs.ExampleDeclarablesProvider;
import io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.events.sent.SentEvent;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeanManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

public class SeApp {

    public static void main(String[] args) throws IOException {

        try (SeContainer seContainer = SeContainerInitializer.newInstance()
                                                             .addBeanClasses(ExampleConnectionFactoryConfigurator.class,
                                                                             ExampleDeclarablesProvider.class,
                                                                             ExampleBindingsConfigurator.class,
                                                                             ExampleEventConsumer.class,
                                                                             ExampleEventProducer.class)
                                                             .initialize();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {


            BeanManager beanManager = seContainer.getBeanManager();
            SentEvent event = new SentEvent();
            event.setMsg("Hello Dukes - " + Instant.now());


            //beanManager.fireEvent(event);

            bufferedReader.readLine();

        }

        System.out.println("Exiting from every thing");
    }


}
