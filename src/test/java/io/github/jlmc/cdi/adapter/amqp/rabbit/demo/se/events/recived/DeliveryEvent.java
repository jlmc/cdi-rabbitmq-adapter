package io.github.jlmc.cdi.adapter.amqp.rabbit.demo.se.events.recived;

public class DeliveryEvent {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "DeliveryEvent{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
