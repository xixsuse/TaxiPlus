package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.widget.LinearLayout;

import java.util.List;

public class Facility {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public class GetFacilities{
        private List<Facility> Facilities;
        private String state;

        public List<Facility> getFacilities() {
            return Facilities;
        }

        public String getState() {
            return state;
        }
    }
}
