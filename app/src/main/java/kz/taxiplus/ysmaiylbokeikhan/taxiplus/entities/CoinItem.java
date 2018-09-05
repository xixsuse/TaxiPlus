package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

public class CoinItem {
    private String date;
    private String coinItems;
    private String mode;
    private String price;
    private String addressFrom;
    private String addressTo;

    public CoinItem(String date, String coinItems, String mode, String price, String addressFrom, String addressTo) {
        this.date = date;
        this.coinItems = coinItems;
        this.mode = mode;
        this.price = price;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCoinItems() {
        return coinItems;
    }

    public void setCoinItems(String coinItems) {
        this.coinItems = coinItems;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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
}
