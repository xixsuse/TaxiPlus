package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

public class HistoryItem {
    private String addressFrom;
    private String addressTo;
    private String date;
    private String price;

    public HistoryItem(String addressFrom, String addressTo, String date, String price) {
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.date = date;
        this.price = price;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
