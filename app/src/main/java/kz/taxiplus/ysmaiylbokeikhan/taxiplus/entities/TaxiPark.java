package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class TaxiPark implements Parcelable{
    private String id;
    private String name;
    private String type;
    private String city_id;
    private String balance;
    private String is_radial;

    protected TaxiPark(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        city_id = in.readString();
        balance = in.readString();
        is_radial = in.readString();
    }

    public static final Creator<TaxiPark> CREATOR = new Creator<TaxiPark>() {
        @Override
        public TaxiPark createFromParcel(Parcel in) {
            return new TaxiPark(in);
        }

        @Override
        public TaxiPark[] newArray(int size) {
            return new TaxiPark[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getBalance() {
        return balance;
    }

    public String getIs_radial() {
        return is_radial;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(city_id);
        dest.writeString(balance);
        dest.writeString(is_radial);
    }

    public class GetTaxiParks{
        private List<TaxiPark> taxi_parks;

        public List<TaxiPark> getTaxi_parks() {
            return taxi_parks;
        }
    }
}
