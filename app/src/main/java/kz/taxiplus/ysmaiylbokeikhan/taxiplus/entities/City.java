package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class City {
    private String _id;

    private String name;

    private String __v;

    private String img;

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String get__v() {
        return __v;
    }

    public String getImg() {
        return img;
    }
    public class Sections{
        @SerializedName("success")
        @Expose
        private Boolean success;

        @SerializedName("sections")
        @Expose
        private List<City> sections = null;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public List<City> getSections() {
            return sections;
        }

        public void setSections(List<City> sections) {
            this.sections = sections;
        }
    }
}
