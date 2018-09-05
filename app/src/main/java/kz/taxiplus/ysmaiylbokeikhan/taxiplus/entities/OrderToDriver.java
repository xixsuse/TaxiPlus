package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

public class OrderToDriver {
    private String status;
    private String last_edit;
    private double from_longitude;
    private String date;
    private double to_latitude;
    private String id;
    private double from_latitude;
    private String price;
    private String created;
    private String user_id;
    private String driver_id;
    private double to_longitude;
    private String is_common;
    private String comment;
    private String taxi_park_id;
    private String order_type;

    public String getStatus() {
        return status;
    }

    public String getLast_edit() {
        return last_edit;
    }

    public double getFrom_longitude() {
        return from_longitude;
    }

    public String getDate() {
        return date;
    }

    public double getTo_latitude() {
        return to_latitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public double getFrom_latitude() {
        return from_latitude;
    }

    public String getPrice() {
        return price;
    }

    public String getCreated() {
        return created;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public double getTo_longitude() {
        return to_longitude;
    }

    public String getIs_common() {
        return is_common;
    }

    public String getComment() {
        return comment;
    }

    public String getTaxi_park_id() {
        return taxi_park_id;
    }

    public String getOrder_type() {
        return order_type;
    }

    public class GetOrderInfo{
        private OrderToDriver order;
        private User client;
        private String state;

        public OrderToDriver getOrder() {
            return order;
        }

        public User getClient() {
            return client;
        }

        public String getState() {
            return state;
        }
    }

}
