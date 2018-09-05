package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class DriverOffer implements Parcelable{
    private String id;
    private String carModel;
    private String carNumber;
    private String driverName;
    private String date;

    public DriverOffer(String id, String name,String carModel, String carNumber, String date) {
        this.id = id;
        this.driverName = name;
        this.carModel = carModel;
        this.carNumber = carNumber;
        this.date = date;
    }

    protected DriverOffer(Parcel in) {
        id = in.readString();
        carModel = in.readString();
        carNumber = in.readString();
        driverName = in.readString();
        date = in.readString();
    }

    public static final Creator<DriverOffer> CREATOR = new Creator<DriverOffer>() {
        @Override
        public DriverOffer createFromParcel(Parcel in) {
            return new DriverOffer(in);
        }

        @Override
        public DriverOffer[] newArray(int size) {
            return new DriverOffer[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(carModel);
        dest.writeString(carNumber);
        dest.writeString(driverName);
        dest.writeString(date);
    }
}
