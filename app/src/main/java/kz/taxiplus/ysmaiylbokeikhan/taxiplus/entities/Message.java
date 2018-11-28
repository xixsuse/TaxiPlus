package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.Date;

public class Message {
    private String message;
    private String time;
    private String from;

    public Message(String message, String from) {
        this.message = message;
        this.time = String.valueOf(new Date().getTime());
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
