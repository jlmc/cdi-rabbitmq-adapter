package io.github.jlmc.cdi.adapter.amqp.rabbit.annotations;

import io.github.jlmc.cdi.adapter.amqp.rabbit.ContentTypes;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface EventMediaType {

    String contentType() default ContentTypes.APPLICATION_OCTET_STREAM;

    /**
     * <pre>
     * SomeInterface bean =
     *     CDI.current()
     *        .select(
     *            SomeInterface.class,
     *            EventMediaTypeLiteral.of("application/json"))
     * ) .get();
     * </pre>
     */
    class EventMediaTypeLiteral extends AnnotationLiteral<EventMediaType> implements EventMediaType {

        private final String contentType;

        private EventMediaTypeLiteral(String contentType) {
            this.contentType = contentType;
        }

        public static EventMediaTypeLiteral of(String contentType) {
            return new EventMediaTypeLiteral(contentType);
        }

        @Override
        public String contentType() {
            return contentType;
        }
    }
}
