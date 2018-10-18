package kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProjectRepository {
    private static ProjectRepository projectRepository;
    private RetrofitInterface repositoryInterface;

    private ProjectRepository(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        repositoryInterface = retrofit.create(RetrofitInterface.class);
    }

    public synchronized static ProjectRepository getInstance() {
        if (projectRepository == null) {
            projectRepository = new ProjectRepository();
        }
        return projectRepository;
    }

    public LiveData<Response> getState(String token, String pushId, LatLng latLng){
        final MutableLiveData<Response> data = new MutableLiveData<>();

        repositoryInterface.getState(token, pushId, latLng.latitude, latLng.longitude, "0").enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
               data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

        return data;
    }

    public LiveData<OrderToDriver.GetOrderInfo> getOrderInfo(String orderId){
        final MutableLiveData<OrderToDriver.GetOrderInfo> data = new MutableLiveData<>();

        repositoryInterface.getOrderInfoCall(orderId).enqueue(new Callback<OrderToDriver.GetOrderInfo>() {
            @Override
            public void onResponse(Call<OrderToDriver.GetOrderInfo> call, retrofit2.Response<OrderToDriver.GetOrderInfo> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<OrderToDriver.GetOrderInfo> call, Throwable t) {

            }
        });

        return data;
    }

    public LiveData<Response> closeSession(String token){
        final MutableLiveData<Response> data = new MutableLiveData<>();

        repositoryInterface.closeSessionCall(token).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

        return data;
    }

    public LiveData<Response> driverCame(String token, String orderId){
        final MutableLiveData<Response> data = new MutableLiveData<>();

        repositoryInterface.driverCameCall(token, orderId).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

        return data;
    }

    public LiveData<Response> driverGo(String token, String orderId){
        final MutableLiveData<Response> data = new MutableLiveData<>();

        repositoryInterface.driverGoCall(token, orderId).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

        return data;
    }

    public LiveData<Response> driverFinish(String token, String orderId){
        final MutableLiveData<Response> data = new MutableLiveData<>();

        repositoryInterface.driverFinisCall(token, orderId).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

        return data;
    }

}
