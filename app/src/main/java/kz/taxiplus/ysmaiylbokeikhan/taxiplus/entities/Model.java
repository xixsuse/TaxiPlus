package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Model implements Parcelable{
    private String id;
    private String model;
    private String parent_id;

    protected Model(Parcel in) {
        id = in.readString();
        model = in.readString();
        parent_id = in.readString();
    }

    public Model() {
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(model);
        dest.writeString(parent_id);
    }

    public class GetModels{
        private String type;
        private List<Model> models;

        public void setType(String type) {
            this.type = type;
        }

        public void setModels(List<Model> models) {
            this.models = models;
        }

        public String getType() {
            return type;
        }

        public List<Model> getModels() {
            return models;
        }
    }
}
