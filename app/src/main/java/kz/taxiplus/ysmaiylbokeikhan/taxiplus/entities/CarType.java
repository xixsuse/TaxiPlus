package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

public class CarType {
    private String type;
    private String title;
    private int image;

    public CarType(String type,String title, int image) {
        this.type = type;
        this.title = title;
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
