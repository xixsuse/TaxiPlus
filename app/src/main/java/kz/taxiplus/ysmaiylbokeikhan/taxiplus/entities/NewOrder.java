package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class NewOrder implements Parcelable{
    private String phone;
    private String user_name;
    private String fromAddress;
    private String toAddress;
    private String price;
    private String date;
    private LatLng fromLatLng;
    private LatLng tomLatLng;

    public NewOrder(String phone, String user_name, String fromAddress, String toAddress, String price, LatLng from, LatLng to) {
        this.phone = phone;
        this.user_name = user_name;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.price = price;
        this.fromLatLng = from;
        this.tomLatLng = to;
    }

    public NewOrder(String phone, String user_name, String fromAddress, String toAddress, String price, String date,LatLng from, LatLng to) {
        this.phone = phone;
        this.user_name = user_name;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.price = price;
        this.date = date;
        this.fromLatLng = from;
        this.tomLatLng = to;
    }

    protected NewOrder(Parcel in) {
        phone = in.readString();
        user_name = in.readString();
        fromAddress = in.readString();
        toAddress = in.readString();
        price = in.readString();
        date = in.readString();
        fromLatLng = in.readParcelable(LatLng.class.getClassLoader());
        tomLatLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<NewOrder> CREATOR = new Creator<NewOrder>() {
        @Override
        public NewOrder createFromParcel(Parcel in) {
            return new NewOrder(in);
        }

        @Override
        public NewOrder[] newArray(int size) {
            return new NewOrder[size];
        }
    };

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LatLng getFromLatLng() {
        return fromLatLng;
    }

    public void setFromLatLng(LatLng fromLatLng) {
        this.fromLatLng = fromLatLng;
    }

    public LatLng getTomLatLng() {
        return tomLatLng;
    }

    public void setTomLatLng(LatLng tomLatLng) {
        this.tomLatLng = tomLatLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(user_name);
        dest.writeString(fromAddress);
        dest.writeString(toAddress);
        dest.writeString(price);
        dest.writeString(date);
        dest.writeParcelable(fromLatLng, flags);
        dest.writeParcelable(tomLatLng, flags);
    }
}
