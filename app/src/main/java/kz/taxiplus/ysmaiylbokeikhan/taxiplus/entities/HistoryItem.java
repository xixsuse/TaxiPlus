package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.List;

public class HistoryItem {
    private String state;
    private List<OrderToDriver> orders;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<OrderToDriver> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderToDriver> orders) {
        this.orders = orders;
    }
}
