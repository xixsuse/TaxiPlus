package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Order implements Parcelable{
    private String id;
    private String name;
    private String phone;
    private String from_longitude;
    private String from_latitude;
    private String to_longitude;
    private String to_latitude;
    private String price;
    private String order_type;
    private String status;
    private String date;
    private long created;

    protected Order(Parcel in) {
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        from_longitude = in.readString();
        from_latitude = in.readString();
        to_longitude = in.readString();
        to_latitude = in.readString();
        price = in.readString();
        order_type = in.readString();
        status = in.readString();
        date = in.readString();
        created = in.readLong();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

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

    public long getCreated() {
        return created;
    }

    public String getOrder_type() {
        return order_type;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(from_longitude);
        dest.writeString(from_latitude);
        dest.writeString(to_longitude);
        dest.writeString(to_latitude);
        dest.writeString(price);
        dest.writeString(order_type);
        dest.writeString(status);
        dest.writeString(date);
        dest.writeLong(created);
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
