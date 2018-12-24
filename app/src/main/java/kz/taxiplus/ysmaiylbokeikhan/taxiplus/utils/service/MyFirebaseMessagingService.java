package kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.objectweb.asm.Handle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.SplashActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Application;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ysmaiylbokeikhan on 28.08.18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String ADMIN_CHANNEL_ID = "ADMIN_CHANNEL_ID";

    CompositeSubscription subscription;
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Paper.init(getApplicationContext());

        String type = remoteMessage.getData().get("type");
        if (type.equals("2")){
            showNotification(remoteMessage, type);
        }else if(type.equals("7")){
            showNotification(remoteMessage, type);
        }else if(type.equals("1")){//silent push notification
            String orderId = remoteMessage.getData().get("order_id");
            if(getLocation() != null){
                subscription = new CompositeSubscription();
                sendLocation(getLocation(), orderId);
            }
        }else if(type.equals("601")){//draw drivers current location
            if(Application.isActivityVisible()) {
                drawDriverLocation(remoteMessage.getData());
            }
        }else if(type.equals("101")) {//new orders
            handleNewOrdersPush(remoteMessage.getData());
        }else{//real push notifications
            if(Application.isActivityVisible()){
                handlePush(remoteMessage.getData());
            }else {
                showNotificationDependType(remoteMessage, type);
            }
        }
    }

    private void handlePush(Map<String, String> data) {
        Intent intent = new Intent("thisIsForMyFragment");
        String orderid = data.get("order_id");
        String type = data.get("type");
        String driverId = data.get("driver_id");

        intent.putExtra(Constants.DRIVERID, driverId);
        intent.putExtra(Constants.ORDERID, orderid);
        intent.putExtra(Constants.TYPE, type);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void drawDriverLocation(Map<String, String> data){
        Intent intent = new Intent("thisIsForMainFragment");
        String latitude = data.get("lat");
        String longitude = data.get("long");

        intent.putExtra(Constants.DRIVERLATITUDE, latitude);
        intent.putExtra(Constants.DRIVERLONGITUDE, longitude);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleNewOrdersPush(Map<String, String> data){
        Intent intent = new Intent("thisIsForCityFragment");
        String orderId = data.get("order_id");

        intent.putExtra(Constants.ORDERID, orderId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void showNotificationDependType(RemoteMessage remoteMessage, String type){
        int notificationId = new Random().nextInt(60000);
        Intent intentResult = new Intent(this, SplashActivity.class);
        intentResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentResult.putExtra(Constants.PENDINGINTENTEXTRA, remoteMessage);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intentResult, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(setNotificationTitle(type))
                .setContentText(setNotificationBody(type))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void showNotification(RemoteMessage remoteMessage, String type){
        int notificationId = new Random().nextInt(60000);
        Intent intentResult = new Intent(this, SplashActivity.class);
        intentResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentResult.putExtra(Constants.PENDINGINTENTEXTRA, remoteMessage);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intentResult, PendingIntent.FLAG_ONE_SHOT);

        String title;
        if(type.equals("2")){
            title = getResources().getString(R.string.new_message_from)+ " "+remoteMessage.getData().get("author");
        }else {
            title = remoteMessage.getData().get("title");
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(remoteMessage.getData().get("text"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }

        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Paper.init(getBaseContext());

        subscription = new CompositeSubscription();
        Paper.book().write(Constants.FIREBASE_TOKEN, s);

        if (!getToken().equals("") && getToken() != null){
            changeFireBaseToken(s);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
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

    private String setNotificationTitle(String type){
        String text = "";
        switch (type){
            case "101":
                text = getResources().getString(R.string.new_order_text);
                break;

            case "201":
                text = getResources().getString(R.string.new_offer);
                break;

            case "301":
                text = getResources().getString(R.string.user_accepted);
                break;

            case "401":
                text = getResources().getString(R.string.driver_is_came);
                break;

            case "501":
                text = getResources().getString(R.string.rate_driver_text);
                break;

            case "0":
                text = getResources().getString(R.string.failed_payment);
                break;
        }
        return text;
    }

    private String setNotificationBody(String type){
        String text = "";
        switch (type){
            case "101":
                text = getResources().getString(R.string.new_order_in_1_km);
                break;

            case "201":
                text = getResources().getString(R.string.new_offer_body);
                break;

            case "301":
                text = "";
                break;

            case "0":
                text = getResources().getString(R.string.pay_by_hand);
                break;
        }
        return text;
    }
}