package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.List;

public class Order {
    private String id;
    private String name;
    private String phone;
    private String from_longitude;
    private String from_latitude;
    private String to_longitude;
    private String to_latitude;
    private String price;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getFrom_longitude() {
        return from_longitude;
    }

    public String getFrom_latitude() {
        return from_latitude;
    }

    public String getTo_longitude() {
        return to_longitude;
    }

    public String getTo_latitude() {
        return to_latitude;
    }

    public String getPrice() {
        return price;
    }

    public class GetOrders{
        private String state;
        private List<Order> orders;

        public String getState() {
            return state;
        }

        public List<Order> getOrders() {
            return orders;
        }
    }
}
