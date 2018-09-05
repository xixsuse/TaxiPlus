package kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities;

import java.util.List;

public class Price {
    private String price;
    private String service_id;
    private String service_name;
    private String img;
    private String img1;

    public String getPrice() {
        return price;
    }

    public String getService_id() {
        return service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public String getImg() {
        return img;
    }

    public String getImg1() {
        return img1;
    }

    public class GetPrices{
        List<Price> price_list;

        public List<Price> getPrice_list() {
            return price_list;
        }
    }
}
