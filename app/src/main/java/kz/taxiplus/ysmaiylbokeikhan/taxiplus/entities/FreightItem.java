package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.List;

public class FreightItem {
    private String id;
    private String phone;
    private String from_string;
    private String to_string;
    private String name;
    private String comment;
    private String date;
    private String price;
    private String submodel;
    private String model;

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getFrom_string() {
        return from_string;
    }

    public String getTo_string() {
        return to_string;
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

    public String getPrice() {
        return price;
    }

    public String getSubmodel() {
        return submodel;
    }

    public String getModel() {
        return model;
    }

    public class CargoResponse{
        private String state;
        private List<FreightItem> chats;
        private AccessPrice price;

        public String getState() {
            return state;
        }

        public List<FreightItem> getChats() {
            return chats;
        }

        public AccessPrice getPrice() {
            return price;
        }
    }
}
