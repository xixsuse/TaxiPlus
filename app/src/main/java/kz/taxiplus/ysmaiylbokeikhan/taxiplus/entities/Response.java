package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.List;

public class Response {
    private int code;
    private int is_active;
    private int is_session_opened;
    private boolean show_chat;
    private String state;
    private String token;
    private String message;
    private String type;
    private String balance;
    private String link;
    private String status;
    private String path;
    private String url;
    private String stars;
    private String rating;
    private String avatar;
    private User user;
    private AccessPrice price;
    private CitiesResponse.City city;
    private List<Car> cars;
    private List<Order> active_orders;
    private Car car;

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

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getIs_session_opened() {
        return is_session_opened;
    }

    public String getLink() {
        return link;
    }

    public String getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public AccessPrice getPrice() {
        return price;
    }

    public CitiesResponse.City getCity() {
        return city;
    }

    public List<Car> getCars() {
        return cars;
    }

    public Car getCar() {
        return car;
    }

    public String getUrl() {
        return url;
    }

    public boolean isShow_chat() {
        return show_chat;
    }

    public String getStars() {
        return stars;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRating() {
        return rating;
    }

    public List<Order> getActive_orders() {
        return active_orders;
    }
}
