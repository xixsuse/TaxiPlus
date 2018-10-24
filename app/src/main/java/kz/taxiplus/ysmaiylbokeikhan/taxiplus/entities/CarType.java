package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

public class CarType {
    private String type;
    private int image;

    public CarType(String type, int image) {
        this.type = type;
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
