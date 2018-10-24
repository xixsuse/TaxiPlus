package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class User implements Parcelable{
    private String id;
    private String name;
    private String role_id;
    private String phone;
    private String token;
    private String balance;
    private String avatar_path;
    private String push_id;
    private String city_id;
    private boolean isSessionOpened;
    private CitiesResponse.City selectedCity;
    private List<Car> cars;

    public User() {
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        role_id = in.readString();
        phone = in.readString();
        token = in.readString();
        balance = in.readString();
        avatar_path = in.readString();
        push_id = in.readString();
        city_id = in.readString();
        isSessionOpened = in.readByte() != 0;
        selectedCity = in.readParcelable(CitiesResponse.City.class.getClassLoader());
        cars = in.createTypedArrayList(Car.CREATOR);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getPhone() {
        return phone;
    }

    public String getToken() {
        return token;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAvatar_path() {
        return avatar_path;
    }

    public String getPush_id() {
        return push_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public boolean isSessionOpened() {
        return isSessionOpened;
    }

    public void setSessionOpened(boolean sessionOpened) {
        isSessionOpened = sessionOpened;
    }

    public CitiesResponse.City getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(CitiesResponse.City selectedCity) {
        this.selectedCity = selectedCity;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<Car> getCars() {
        return cars;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(role_id);
        dest.writeString(phone);
        dest.writeString(token);
        dest.writeString(balance);
        dest.writeString(avatar_path);
        dest.writeString(push_id);
        dest.writeString(city_id);
        dest.writeByte((byte) (isSessionOpened ? 1 : 0));
        dest.writeParcelable(selectedCity, flags);
        dest.writeTypedList(cars);
    }


    public class GetFullInfo{
        private User user;
        private CitiesResponse.City city;
        private String taxi_park;
        private List<String> facilities;
        private List<Car> cars;

        public User getUser() {
            return user;
        }

        public CitiesResponse.City getCity() {
            return city;
        }

        public List<String> getFacilities() {
            return facilities;
        }

        public String getTaxi_park() {
            return taxi_park;
        }

        public List<Car> getCars() {
            return cars;
        }
    }
}
