package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Car implements Parcelable{
    private String id;
    private String body;
    private String model;
    private String submodel;
    private String car_id;
    private String tonns;
    private String seats_number;
    private String year;
    private String number;
    private String type;

    protected Car(Parcel in) {
        id = in.readString();
        body = in.readString();
        model = in.readString();
        submodel = in.readString();
        car_id = in.readString();
        tonns = in.readString();
        seats_number = in.readString();
        year = in.readString();
        number = in.readString();
        type = in.readString();
    }

    public Car() {
    }

    public Car(String model, String submodel, String seats_number, String year, String number, String type) {
        this.model = model;
        this.submodel = submodel;
        this.seats_number = seats_number;
        this.year = year;
        this.number = number;
        this.type = type;
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getModel() {
        return model;
    }

    public String getCar_id() {
        return car_id;
    }

    public String getTonns() {
        return tonns;
    }

    public String getSeats_number() {
        return seats_number;
    }

    public String getYear() {
        return year;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getSubmodel() {
        return submodel;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSubmodel(String submodel) {
        this.submodel = submodel;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public void setTonns(String tonns) {
        this.tonns = tonns;
    }

    public void setSeats_number(String seats_number) {
        this.seats_number = seats_number;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(body);
        dest.writeString(model);
        dest.writeString(submodel);
        dest.writeString(car_id);
        dest.writeString(tonns);
        dest.writeString(seats_number);
        dest.writeString(year);
        dest.writeString(number);
        dest.writeString(type);
    }
}
