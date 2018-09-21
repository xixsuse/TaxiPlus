package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.Date;

public class Message {
    private String message;
    private long time;
    private String from;

    public Message(String message, String from) {
        this.message = message;
        this.time = new Date().getTime();
        this.from = from;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
