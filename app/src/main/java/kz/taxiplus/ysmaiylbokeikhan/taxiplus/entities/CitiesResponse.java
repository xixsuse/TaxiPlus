package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CitiesResponse {
    private String state;
    private List<City> cities;

    public String getState() {
        return state;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public class City implements Parcelable{
        private String id;
        private String cname;
        private String region_id;
        private String name;

        public City() {
        }

        protected City(Parcel in) {
            id = in.readString();
            cname = in.readString();
            region_id = in.readString();
            name = in.readString();
        }

        public final Creator<City> CREATOR = new Creator<City>() {
            @Override
            public City createFromParcel(Parcel in) {
                return new City(in);
            }

            @Override
            public City[] newArray(int size) {
                return new City[size];
            }
        };

        public String getId() {
            return id;
        }

        public String getCname() {
            return cname;
        }

        public String getRegion_id() {
            return region_id;
        }

        public String getName() {
            return name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(cname);
            dest.writeString(region_id);
            dest.writeString(name);
        }
    }
}
