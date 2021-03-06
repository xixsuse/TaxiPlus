package kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository;

import java.util.HashMap;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DirectionResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DriverBalance;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Facility;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.FreightItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.HistoryItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.IntercityOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Model;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewsItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OnLineResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Price;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.CitiesResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.ResponsePrice;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.SessionPrices;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.TaxiPark;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

public interface RetrofitInterface {
    @GET("get-car-models/")
    Observable<Model.GetModels> getModels();

    @GET("get-regions/")
    Observable<CitiesResponse> getCities();

    @GET("get-car-submodels/")
    Observable<Model.GetModels> getSubmodels(@Query("id") String model_id);

    @GET("get-facilities/")
    Observable<Facility.GetFacilities> getFacilities();

    @GET("get-taxi-parks/")
    Observable<TaxiPark.GetTaxiParks> getTaxiParks();

    @FormUrlEncoded
    @POST("send-sms/")
    Observable<Response> authFirstStep(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("verify-code")
    Observable<Response> authSecondStep(@Field("phone") String phone, @Field("code")String code);

    @FormUrlEncoded
    @POST("sign-up/")
    Observable<Response> authThirdStep(@Field("phone") String phone,
                                       @Field("name")String name,
                                       @Field("city_id") String city_id);

    @FormUrlEncoded
    @POST("change-role/")
    Observable<Response> changeRole(@Field("token") String token, @Field("role_id")String role_id);

    @FormUrlEncoded
    @POST("get-addresses/")
    Observable<Place.GetPlaces> getFavPlaces(@Field("token") String token);

    @FormUrlEncoded
    @POST("save-address/")
    Observable<Response> addFavPlaces(@Field("token") String token, @Field("address") String address,
                                      @Field("latitude") double latitude, @Field("longitude") double longitude);

    @FormUrlEncoded
    @POST("get-price/")
    Observable<Price.GetPrices> getPrices(@Field("token") String token,
                                         @Field("latitude_a") double latitude_a,
                                         @Field("longitude_a") double longitude_a,
                                         @Field("latitude_b") double latitude_b,
                                         @Field("longitude_b") double longitude_b,
                                         @Field("type") int type);

    @Headers( "Content-Type: application/json" )
    @POST("driver-sign-up/")
    Observable<Response> driverRegistration(@Body HashMap<String, Object> body);

    @FormUrlEncoded
    @POST("set-push-id/")
    Observable<Response> sendFirebasePush(@Field("token")String token, @Field("push_id")String push_id);

    @FormUrlEncoded
    @POST("start-session/")
    Observable<Response> startSession(@Field("token")String token, @Field("duration")String duration);

    @FormUrlEncoded
    @POST("start-session/")
    Observable<Response> startSession(@Field("token")String token);

    @FormUrlEncoded
    @POST("get-user/")
    Observable<User.GetFullInfo> getUser(@Field("token")String token);

    @FormUrlEncoded
    @POST("close-session/")
    Observable<Response> closeSession(@Field("token")String token);

    @FormUrlEncoded
    @POST("close-session/")
    Call<Response> closeSessionCall(@Field("token")String token);

    @FormUrlEncoded
    @POST("get-session-price/")
    Observable<SessionPrices> getSessionPrices(@Field("token")String token);

    @FormUrlEncoded
    @POST("accept-driver/")
    Observable<OrderToDriver.GetOrderInfo> acceptDriver(@Field("order_id")String order_id,
                                                        @Field("driver_id")String driver_id);

    @FormUrlEncoded
    @POST("accept-order/")
    Observable<Response> acceptOrderDriver(@Field("token")String token,
                                           @Field("order_id")String order_id);

    @FormUrlEncoded
    @POST("rate-driver/")
    Observable<Response> rateDriver(@Field("token")String token,
                                    @Field("order_id")String order_id,
                                    @Field("text")String text,
                                    @Field("value") String value);

    @FormUrlEncoded
    @POST("check-location/")
    Observable<Response> sendLoaction(@Field("token")String token,
                                      @Field("latitude")double latitude,
                                      @Field("longitude") double longitude,
                                      @Field("order_id")String order_id);
    @FormUrlEncoded
    @POST("get-order-info/")
    Observable<OrderToDriver.GetOrderInfo> getOrderInfo(@Field("order_id")String order_id);

    @FormUrlEncoded
    @POST("get-order-info/")
    Call<OrderToDriver.GetOrderInfo> getOrderInfoCall(@Field("order_id")String order_id);

    @FormUrlEncoded
    @POST("get-driver-info/")
    Observable<OrderToDriver.GetOrderInfo> getDriverInfo(@Field("driver_id")String driver_id);

    @FormUrlEncoded
    @POST("set-push-id/")
    Observable<Response> checkState(@Field("token")String token,
                                    @Field("push_id")String push_id,
                                    @Field("lat")double lat,
                                    @Field("long")double longi,
                                    @Field("platform") String platform);

    @FormUrlEncoded
    @POST("set-push-id/")
    Observable<Response> checkStateDriver(@Field("token")String token,
                                    @Field("push_id")String push_id,
                                    @Field("platform") String platform);

    @FormUrlEncoded
    @POST("set-push-id/")
    Call<Response> getState(@Field("token")String token,
                                    @Field("push_id")String push_id,
                                    @Field("lat")double lat,
                                    @Field("long")double longi,
                                    @Field("platform") String platform);

    @FormUrlEncoded
    @POST("driver-came/")
    Observable<Response> driverCame(@Field("token")String token, @Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("driver-came/")
    Call<Response> driverCameCall(@Field("token")String token, @Field("order_id") String order_id);


    @FormUrlEncoded
    @POST("go/")
    Observable<Response> driverGo(@Field("token")String token, @Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("go/")
    Call<Response> driverGoCall(@Field("token")String token, @Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("finish-order/")
    Observable<Response> driverFinisg(@Field("token")String token, @Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("finish-order/")
    Call<Response> driverFinisCall(@Field("token")String token, @Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("make-order/")
    Observable<Response> makeOrder(@Field("token")String token,
                                   @Field("latitude_a")double latitude_a,
                                   @Field("longitude_a")double longitude_a,
                                   @Field("latitude_b")double latitude_b,
                                   @Field("longitude_b")double longitude_b,
                                   @Field("service_id")int service_id,
                                   @Field("comment")String comment,
                                   @Field("date")long date,
                                   @Field("payment_type")int payment_type);

    @FormUrlEncoded
    @POST("make-order/")
    Observable<Response> makeOrderSobber(@Field("token")String token,
                                         @Field("latitude_a")double latitude_a,
                                         @Field("longitude_a")double longitude_a,
                                         @Field("latitude_b")double latitude_b,
                                         @Field("longitude_b")double longitude_b,
                                         @Field("service_id")int service_id,
                                         @Field("comment")String comment,
                                         @Field("date")long date,
                                         @Field("payment_type")int payment_type,
                                         @Field("kpp") int kpp_type);

    @FormUrlEncoded
    @POST("get-referal-link/")
    Observable<Response> getReferalLink(@Field("token")String token);


    @FormUrlEncoded
    @POST("get-own-orders/")
    Observable<Order.GetOrders> getOwnOrders(@Field("token")String token);


    @FormUrlEncoded
    @POST("get-shared-orders/")
    Observable<Order.GetOrders> getSharedOrders(@Field("token")String token);

    @FormUrlEncoded
    @POST("add-complaint/")
    Observable<Response> addComplaint(@Field("token")String token,
                                      @Field("text")String text,
                                      @Field("order_id")String orderId);
    @FormUrlEncoded
    @POST("reject-order/")
    Observable<Response> rejectOrder(@Field("token")String token,
                                     @Field("order_id")String orderId);

    @FormUrlEncoded
    @POST("cancel-order/")
    Observable<Response> cancelOrder(@Field("token")String token,
                                     @Field("order_id")String orderId);
    @FormUrlEncoded
    @POST("get-all-orders/")
    Observable<HistoryItem> getHistory(@Field("token")String token, @Field("date")String date);

    @FormUrlEncoded
    @POST("get-my-balance/")
    Observable<DriverBalance> getBalance(@Field("token")String token);

    @FormUrlEncoded
    @POST("get-active-orders/")
    Observable<OrderToDriver.GetOrders> getActiveOrders(@Field("token")String token);

    @FormUrlEncoded
    @POST("get-specific-chats/")
    Observable<DirectionResponse> getDirections(@Field("token")String token, @Field("type")String type);

    @FormUrlEncoded
    @POST("get-mejdugorodniy-chat/")
    Observable<IntercityOrder.InterCityOrdersResponse> getIntercityOrders(@Field("token")String token,
                                                                          @Field("start_id")String start_id,
                                                                          @Field("end_id")String end_id);
    @FormUrlEncoded
    @POST("get-specific-chats/")
    Observable<FreightItem.CargoResponse> getFreights(@Field("token")String token,
                                                      @Field("type")String type);

    @FormUrlEncoded
    @POST("get-specific-chats/")
    Observable<FreightItem.CargoResponse> getInvaOrders(@Field("token")String token,
                                                      @Field("type")String type);

    @FormUrlEncoded
    @POST("add-recomendation/")
    Observable<Response> addRecomendation(@Field("token")String token,
                                          @Field("text") String text,
                                          @Field("rating") String rating);

    @FormUrlEncoded
    @POST("buy-access/")
    Observable<Response> buyAccess(@Field("token")String token,
                                                  @Field("type") String type,
                                                  @Field("publish") String publish);

    @FormUrlEncoded
    @POST("logout/")
    Observable<Response> logout(@Field("token")String token);

    @FormUrlEncoded
    @POST("get-amount/")
    Observable<OnLineResponse> getOnLineCount(@Field("token")String token);

    @FormUrlEncoded
    @POST("get-news/")
    Observable<NewsItem.NewsResponse> getNews(@Field("token")String token);

    @FormUrlEncoded
    @POST("how-many-chats/")
    Observable<Response> howManyChats(@Field("token")String token);

    @FormUrlEncoded
    @POST("message/")
    Observable<Response> sendMessage(@Field("token") String token,
                                     @Field("phone")String phone,
                                     @Field("text") String text);

    @FormUrlEncoded
    @POST("money-request/")
    Observable<Response> sendMoney(@Field("token") String token,
                                     @Field("amount")String amount,
                                     @Field("card_number") String card_number);

    @FormUrlEncoded
    @POST("add-specific-order/")
    Observable<Response> addIntercityOrder(@Field("token")String token,
                                                  @Field("type") String type,
                                                  @Field("seats_number") String seats_number,
                                                  @Field("start_id") String start_id,
                                                  @Field("end_id") String end_id,
                                                  @Field("price") String price,
                                                  @Field("date") long date,
                                                  @Field("comment") String comment
                                           );

    @FormUrlEncoded
    @POST("add-specific-order/")
    Observable<Response> addCargo(@Field("token")String token,
                                           @Field("type") String type,
                                           @Field("price") String price,
                                           @Field("date") long date,
                                           @Field("start_string") String start_string,
                                           @Field("end_string") String end_string,
                                           @Field("comment") String comment
                                    );

    @FormUrlEncoded
    @POST("get-trezvy-price/")
    Observable<ResponsePrice> getSoberPrice(@Field("token")String token,
                                            @Field("latitude_a")double latitude_a,
                                            @Field("longitude_a")double longitude_a,
                                            @Field("latitude_b")double latitude_b,
                                            @Field("longitude_b")double longitude_b,
                                            @Field("comment")String comment);

    @Multipart
    @POST("upload-avatar/")
    Call<Response> uploadAva(@Part MultipartBody.Part file, @Part("token") RequestBody token);
}
