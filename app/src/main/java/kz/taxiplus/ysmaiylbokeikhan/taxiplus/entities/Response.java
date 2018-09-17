package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

public class Response {
    private String state;
    private String token;
    private String message;
    private String type;
    private String balance;
    private User user;
    private int code;
    private int is_active;
    private int is_session_opened;

    public String getState() {
        return state;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public User getUser() {
        return user;
    }

    public int getIs_active() {
        return is_active;
    }

    public String getBalance() {
        return balance;
    }

    public int getIs_session_opened() {
        return is_session_opened;
    }
}
