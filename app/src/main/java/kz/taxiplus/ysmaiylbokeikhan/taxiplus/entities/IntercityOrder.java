package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.List;

public class IntercityOrder {
    private String id;
    private String submodel;
    private String model;
    private String phone;
    private String start;
    private String seats_number;
    private String name;
    private String comment;
    private String date;
    private String end;
    private String price;

    public String getId() {
        return id;
    }

    public String getSubmodel() {
        return submodel;
    }

    public String getModel() {
        return model;
    }

    public String getPhone() {
        return phone;
    }

    public String getStart() {
        return start;
    }

    public String getSeats_number() {
        return seats_number;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getEnd() {
        return end;
    }

    public String getPrice() {
        return price;
    }

    public class InterCityOrdersResponse{
            private String state;
            private AccessPrice price;
            private List<IntercityOrder> orders;

        public String getState() {
            return state;
        }

        public List<IntercityOrder> getOrders() {
            return orders;
        }

        public AccessPrice getAccessPrice() {
            return price;
        }
    }
}
