package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

public class RecyclerMenuItem {
    public String title;
    public int logo;
    public int index;

    public RecyclerMenuItem(String title, int logo, int index) {
        this.title = title;
        this.logo = logo;
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
