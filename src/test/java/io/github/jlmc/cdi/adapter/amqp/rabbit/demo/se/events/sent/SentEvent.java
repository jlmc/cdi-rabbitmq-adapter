package io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.events.sent;

import java.io.Serializable;

public class SentEvent implements Serializable {

    private String msg;

    public SentEvent() {
    }

    private SentEvent(String msg) {
        this.msg = msg;
    }

    public static SentEvent of(String msg) {
        return new SentEvent(msg);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
