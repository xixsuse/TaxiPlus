package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.ProjectRepository;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;

public class UserMainViewModel extends AndroidViewModel implements DirectionCallback {
    private LiveData<Response> responseLiveData;
    private LiveData<Response> responseSessionLiveData;
    private LiveData<Response> responseDriverGoLiveData;
    private LiveData<Response> responseDriverCameLiveData;
    private LiveData<Response> responseDriverFinishLiveData;
    private MutableLiveData<Direction> directionLiveData = new MutableLiveData<>();
    private LiveData<OrderToDriver.GetOrderInfo> orderInfoLiveData;
    private Context context;

    public UserMainViewModel(@NonNull Application application, Context context) {
        super(application);
        this.context = context;

        responseLiveData = ProjectRepository.getInstance().getState(Utility.getToken(context), Utility.getPushId(context),
                Utility.getLocation(context));
    }

    //requests
    public LiveData<Response> getResponseLiveData(){
        return responseLiveData;
    }


    public void sentRequestToOrderInfo(String orderId){
        this.orderInfoLiveData = ProjectRepository.getInstance().getOrderInfo(orderId);
    }

    public LiveData<OrderToDriver.GetOrderInfo> getOrderInfoLiveData(){
        return orderInfoLiveData;
    }


    public void closeSession(){
        this.responseSessionLiveData = ProjectRepository.getInstance().closeSession(Utility.getToken(context));
    }

    public LiveData<Response> getCloseSessionResponse(){
        return responseSessionLiveData;
    }

    public void driverCame(String orderId){
        this.responseDriverCameLiveData = ProjectRepository.getInstance().driverCame(Utility.getToken(context), orderId);
    }

    public LiveData<Response> getDriderCameResponse(){
        return responseDriverCameLiveData;
    }

    public void driverGo(String orderId){
        this.responseDriverGoLiveData = ProjectRepository.getInstance().driverGo(Utility.getToken(context), orderId);
    }

    public LiveData<Response> getDriderGoResponse(){
        return responseDriverGoLiveData;
    }

    public void driverFinish(String orderId){
        this.responseDriverFinishLiveData = ProjectRepository.getInstance().driverFinish(Utility.getToken(context), orderId);
    }

    public LiveData<Response> getDriderFinishResponse(){
        return responseDriverFinishLiveData;
    }


    public void sendRequest(LatLng origin, LatLng destination) {
        GoogleDirection.withServerKey(Constants.GOOGLE_API_KEY)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(false)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            directionLiveData.postValue(direction);
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    public LiveData<Direction> getDirection(){
        return directionLiveData;
    }


    public static class MyViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private Context context;


        public MyViewModelFactory(Application application, Context context) {
            mApplication = application;
            this.context = context;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new UserMainViewModel(mApplication, context);
        }
    }
}
