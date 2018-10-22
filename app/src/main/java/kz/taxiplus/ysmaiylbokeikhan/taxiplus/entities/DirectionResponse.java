package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class DirectionResponse {
    private String state;
    private List<Direction> chats;

    public String getState() {
        return state;
    }

    public List<Direction> getChats() {
        return chats;
    }

    public class Direction implements Parcelable{
        private String id;
        private String end_id;
        private String start_id;
        private String start;
        private String end;

        public Direction() {
        }

        protected Direction(Parcel in) {
            id = in.readString();
            end_id = in.readString();
            start_id = in.readString();
            start = in.readString();
            end = in.readString();
        }

        public final Creator<Direction> CREATOR = new Creator<Direction>() {
            @Override
            public Direction createFromParcel(Parcel in) {
                return new Direction(in);
            }

            @Override
            public Direction[] newArray(int size) {
                return new Direction[size];
            }
        };

        public String getId() {
            return id;
        }

        public String getEnd_id() {
            return end_id;
        }

        public String getStart_id() {
            return start_id;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(end_id);
            dest.writeString(start_id);
            dest.writeString(start);
            dest.writeString(end);
        }
    }
}
