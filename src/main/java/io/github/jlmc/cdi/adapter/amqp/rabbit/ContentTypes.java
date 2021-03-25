package io.github.jlmc.cdi.adapter.amqp.rabbit;

public final class ContentTypes {

    public static final String TEXT_PLAIN = "text/plain";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    private ContentTypes() {
        throw new AssertionError();
    }
}
