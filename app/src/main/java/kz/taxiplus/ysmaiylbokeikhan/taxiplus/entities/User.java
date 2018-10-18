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
    private String car;
    private String avatar_path;
    private String car_number;
    private String push_id;
    private String city_id;
    private String car_year;
    private boolean isSessionOpened;

    public User() {
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        role_id = in.readString();
        phone = in.readString();
        token = in.readString();
        balance = in.readString();
        car = in.readString();
        avatar_path = in.readString();
        car_number = in.readString();
        push_id = in.readString();
        city_id = in.readString();
        car_year = in.readString();
        isSessionOpened = in.readByte() != 0;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getAvatar_path() {
        return avatar_path;
    }

    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCar_year() {
        return car_year;
    }

    public void setCar_year(String car_year) {
        this.car_year = car_year;
    }

    public boolean isSessionOpened() {
        return isSessionOpened;
    }

    public void setSessionOpened(boolean sessionOpened) {
        isSessionOpened = sessionOpened;
    }

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
        dest.writeString(car);
        dest.writeString(avatar_path);
        dest.writeString(car_number);
        dest.writeString(push_id);
        dest.writeString(city_id);
        dest.writeString(car_year);
        dest.writeByte((byte) (isSessionOpened ? 1 : 0));
    }

    public class GetFullInfo{
        private User user;
        private String model;
        private String submodel;
        private String taxi_park;
        private List<String> facilities;

        public User getUser() {
            return user;
        }

        public String getModel() {
            return model;
        }

        public String getSubmodel() {
            return submodel;
        }

        public List<String> getFacilities() {
            return facilities;
        }

        public String getTaxi_park() {
            return taxi_park;
        }
    }
}
