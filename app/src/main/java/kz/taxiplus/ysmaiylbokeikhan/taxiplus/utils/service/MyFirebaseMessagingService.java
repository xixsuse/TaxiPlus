package kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Application;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ysmaiylbokeikhan on 28.08.18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String ADMIN_CHANNEL_ID = "ADMIN_CHANNEL_ID";
    private static final String TAG = "MyFirebaseIIDService";

    CompositeSubscription subscription;
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }

        String type = remoteMessage.getData().get("type");

        //silent push notification
        if(type.equals("1")){
            String orderId = remoteMessage.getData().get("order_id");
            if(getLocation() != null){
                subscription = new CompositeSubscription();
                sendLocation(getLocation(), orderId);
            }
        }else if(type.equals("101")){//new order in 1km
            if(Application.isActivityVisible()){
                getOrder(remoteMessage.getData());
            }else {
                int notificationId = new Random().nextInt(60000);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.new_order_text))
                    .setContentText(getResources().getString(R.string.new_order_in_1_km))
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        }

    }

    private void getOrder(Map<String, String> data) {
        String orderid = data.get("order_id");

        String filter = "thisIsForMyFragment";
        Intent intent = new Intent(filter);
        intent.putExtra(Constants.ORDERID, orderid);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Paper.init(getBaseContext());

        subscription = new CompositeSubscription();

        changeFireBaseToken(s);
        Paper.book().write(Constants.FIREBASE_TOKEN, s);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true); adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    //requests to server
    private void changeFireBaseToken(String push_id) {
        subscription.add(NetworkUtil.getRetrofit()
                .sendFirebasePush(getToken(), push_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseChange, this::handleErrorChange));
    }

    private void handleResponseChange(Response response) {

    }

    private void handleErrorChange(Throwable throwable) {

    }

    private void sendLocation(LatLng latLng, String orderId) {
        subscription.add(NetworkUtil.getRetrofit()
                .sendLoaction(getToken(), latLng.latitude, latLng.longitude, orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLoc, this::handleErrorLoc));
    }

    private void handleResponseLoc(Response response) {

    }

    private void handleErrorLoc(Throwable throwable) {

    }

    //helper functions
    private String getToken() {
        String token = "";
        User user = Paper.book().read(Constants.USER);

        if (user != null) {
            token = user.getToken();
        }

        return token;
    }

    private LatLng getLocation(){
        LatLng latLng = Paper.book().read(Constants.MYLOCATION);

        return latLng;
    }
}