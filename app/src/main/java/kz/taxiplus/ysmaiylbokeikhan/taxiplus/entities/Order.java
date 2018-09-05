package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable{
    private Place toAddess;
    private Place fromAddess;
    private int mode;
    private String comment;
    private long date;
    private String cost;
    private String carModel;
    private String volume;
    private String weight;
    private String toAddressString;
    private String fromAddressString;
    private String status;

    public Order(Place toAddess, Place fromAddess, int mode, String comment, long date) {
        this.toAddess = toAddess;
        this.fromAddess = fromAddess;
        this.mode = mode;
        this.comment = comment;
        this.date = date;
    }

    public Order(Place toAddess, Place fromAddess, int mode, String comment, long date, String volume, String weight) {
        this.toAddess = toAddess;
        this.fromAddess = fromAddess;
        this.mode = mode;
        this.comment = comment;
        this.date = date;
        this.volume = volume;
        this.weight = weight;
    }

    public Order(Place toAddess, Place fromAddess, int mode, String comment, long date, String carModel) {
        this.toAddess = toAddess;
        this.fromAddess = fromAddess;
        this.mode = mode;
        this.comment = comment;
        this.date = date;
        this.carModel = carModel;
    }

    public Order() {
    }

    protected Order(Parcel in) {
        toAddess = in.readParcelable(Address.class.getClassLoader());
        fromAddess = in.readParcelable(Address.class.getClassLoader());
        mode = in.readInt();
        comment = in.readString();
        date = in.readLong();
        cost = in.readString();
        carModel = in.readString();
        volume = in.readString();
        weight = in.readString();
        toAddressString = in.readString();
        fromAddressString = in.readString();
        status = in.readString();
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

    public Place getToAddess() {
        return toAddess;
    }

    public void setToAddess(Place toAddess) {
        this.toAddess = toAddess;
    }

    public Place getFromAddess() {
        return fromAddess;
    }

    public void setFromAddess(Place fromAddess) {
        this.fromAddess = fromAddess;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getToAddressString() {
        return toAddressString;
    }

    public void setToAddressString(String toAddressString) {
        this.toAddressString = toAddressString;
    }

    public String getFromAddressString() {
        return fromAddressString;
    }

    public void setFromAddressString(String fromAddressString) {
        this.fromAddressString = fromAddressString;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(toAddess, flags);
        dest.writeParcelable(fromAddess, flags);
        dest.writeInt(mode);
        dest.writeString(comment);
        dest.writeLong(date);
        dest.writeString(cost);
        dest.writeString(carModel);
        dest.writeString(volume);
        dest.writeString(weight);
        dest.writeString(toAddressString);
        dest.writeString(fromAddressString);
        dest.writeString(status);
    }
}
