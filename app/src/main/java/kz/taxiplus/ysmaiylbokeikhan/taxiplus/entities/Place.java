package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Place implements Parcelable{
    private String address;
    private String user_id;
    private double longitude;
    private double latitude;

    public Place(String address, double latitude, double  longitude) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Place(String address) {
        this.address = address;
    }

    protected Place(Parcel in) {
        address = in.readString();
        user_id = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(user_id);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    public class GetPlaces{
        private String state;
        private List<Place> addresses;

        public String getState() {
            return state;
        }

        public List<Place> getAddresses() {
            return addresses;
        }
    }
}
