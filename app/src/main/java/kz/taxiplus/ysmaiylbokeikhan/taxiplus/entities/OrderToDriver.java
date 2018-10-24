package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

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

    public static class GetOrderInfo implements Parcelable{
        private OrderToDriver order;
        private User client;
        private User driver;
        private List<Car> car;
        private String state;

        protected GetOrderInfo(Parcel in) {
            state = in.readString();
        }

        public static final Creator<GetOrderInfo> CREATOR = new Creator<GetOrderInfo>() {
            @Override
            public GetOrderInfo createFromParcel(Parcel in) {
                return new GetOrderInfo(in);
            }

            @Override
            public GetOrderInfo[] newArray(int size) {
                return new GetOrderInfo[size];
            }
        };

        public OrderToDriver getOrder() {
            return order;
        }

        public User getClient() {
            return client;
        }

        public String getState() {
            return state;
        }

        public User getDriver() {
            return driver;
        }

        public List<Car> getCar() {
            return car;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(state);
        }
    }

    public static class GetOrders{
        private String state;
        private List<OrderToDriver> orders;

        public String getState() {
            return state;
        }

        public List<OrderToDriver> getOrders() {
            return orders;
        }
    }
}
