package kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;

public class Utility {
    public static void setTheme(Context context, int theme) {
        Paper.init(context);
        Paper.book().write(context.getString(R.string.prefs_theme_key), theme);
    }
    public static int getTheme(Context context) {
        Paper.init(context);
        return Paper.book().read(context.getString(R.string.prefs_theme_key), 1);
    }

    public static String getToken(Context context) {
        Paper.init(context);
        User user = Paper.book().read(Constants.USER);
        Log.d("currentToken", user.getToken());

        return user.getToken();
    }

    public static String getPushId(Context context) {
        Paper.init(context);
        String push_id = Paper.book().read(Constants.FIREBASE_TOKEN, "");

        return push_id;
    }

    public static LatLng getLocation(Context context) {
        Paper.init(context);
        LatLng latLng = Paper.book().read(Constants.MYLOCATION, new LatLng(0,0));

        return latLng;
    }

    public static void dismissKeyboard(Activity activity){
        View v = activity.getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static boolean checkGPSPermission(Context context) {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean checkCallPermission(Context context) {
        String permission = "android.permission.CALL_PHONE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean checkPermissionReadImage(Context context) {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static Address getAddressFromLatLng(LatLng latLng, Context context){
        List<Address> addressList = null;
        Address addresReturn = null;
        Geocoder geocoder = new Geocoder(context, context.getResources().getConfiguration().locale);

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addressList.size() > 0) {
                addresReturn = addressList.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresReturn;
    }

    public static String getAddressFromLatLngStr(LatLng latLng, Context context){
        List<Address> addressList;
        Address addresReturn = null;
        String title = "";

        Geocoder geocoder = new Geocoder(context, context.getResources().getConfiguration().locale);

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addressList.size()>0) {
                addresReturn = addressList.get(0);
                title = addresReturn.getAddressLine(0).substring(0, addresReturn.getAddressLine(0).indexOf(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return title;
    }

    public static Bitmap setIcon(int src, Context context){
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), src);
        return Bitmap.createScaledBitmap(icon, 125, 100, false);
    }

    public static String setOrder(String order_type, Context context) {
        String typeString = "";
        switch (order_type){
            case "1":
                typeString = context.getResources().getString(R.string.econom_mode);
                break;

            case "2":
                typeString = context.getResources().getString(R.string.business_mode);
                break;

            case "3":
                typeString = context.getResources().getString(R.string.corp_mode);
                break;

            case "4":
                typeString = context.getResources().getString(R.string.modeLadyTaxi);
                break;

            case "5":
                typeString = context.getResources().getString(R.string.modeInvaTaxi);
                break;

            case "6":
                typeString = context.getResources().getString(R.string.modeCitiesTaxi);
                break;

            case "7":
                typeString = context.getResources().getString(R.string.modeCargoTaxi);
                break;

            case "8":
                typeString = context.getResources().getString(R.string.modeEvo);
                break;
        }
        return typeString;
    }

    public static String setDataString(String miliseconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(miliseconds));
        return formatter.format(calendar.getTime());
    }
}