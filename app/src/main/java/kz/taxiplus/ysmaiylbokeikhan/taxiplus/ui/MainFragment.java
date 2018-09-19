package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.OpenSessionFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.FromAndToFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.ModesDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.SelectModeFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.ChatFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class MainFragment extends Fragment implements OnMapReadyCallback, DirectionCallback {
    public static final String TAG = Constants.MAINFRAGMENTTAG;
    private static final String ORDERINFO = "order_info";
    public static final int REQUEST_GPS_PERMISSION = 123;
    private static final int REQUEST_CALL_PERMISSION = 103;
    private static final String NULLTAG = "NullPointerException";

    private LatLng myLocation;
    private int drawerCounter = 0;
    private int driverButtonType = 0;
    private int theme;
    private NewOrder order;
    private User user;
    private LatLng to, from;
    private Place fromAddress, toAddress;
    private boolean isClickableA = false;
    private boolean isClickableB = false;
    private OrderToDriver.GetOrderInfo orderInfo;
    private String orderId;
    private String mainState;

    private Marker mPositionMarker, markerTo, markerFrom, driverCarMarker;
    public MapView mapView;
    private GoogleMap map;
    private View view;

    private ImageView myLocationIcon;
    private LinearLayout menuIcon, openSessionView;
    private ConstraintLayout newOrderView;
    private Button fromButton, toButton, modeButton;
    private TextView sessionText;
    private ProgressBar progressBar;
    private BottomSheetBehavior sheetBehavior;
    private ConstraintLayout layoutBottomSheet;
    private TextView confirmFromText, confirmToText, confNameText;
    private TextView confPhoneText, confModelText, confNumberText;
    private TextView confDateText, confModeText;
    private Button cameButton;
    private ImageButton confCallButton, chatButton;

    private FragmentTransaction fragmentTransaction;
    private CompositeSubscription subscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = getArguments().getParcelable(Constants.NEWORDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(view);

        mapView = view.findViewById(R.id.mf_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        if (checkGPSPermission()) {
            mapView.getMapAsync(this);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
        }

        return view;
    }

    private void initViews(View view) {
//        Paper.book().delete(Constants.LASTPLACES);
        subscription = new CompositeSubscription();
        menuIcon = view.findViewById(R.id.mf_menu_icon);
        myLocationIcon = view.findViewById(R.id.mf_my_location);
        newOrderView = view.findViewById(R.id.mf_new_order_view);
        toButton = view.findViewById(R.id.mf_to_button);
        fromButton = view.findViewById(R.id.mf_from_button);
        modeButton = view.findViewById(R.id.mf_select_mode_button);
        openSessionView = view.findViewById(R.id.mf_open_session_view);
        sessionText = view.findViewById(R.id.mf_open_session_text);
        progressBar = view.findViewById(R.id.mf_progressbar);
        cameButton = view.findViewById(R.id.mf_came_button);
        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myBroadcastReceiver,
                new IntentFilter("thisIsForMainFragment"));

        theme = Paper.book().read(getString(R.string.prefs_theme_key), 1);
        user = Paper.book().read(Constants.USER);

        if(user.isSessionOpened()){
            sessionText.setText(getResources().getString(R.string.close_session_event));
        }else {
            sessionText.setText(getResources().getString(R.string.open_session_event));
        }
        setListeners();
    }

    private void setViewsVisibility() {
        if (order != null) {
            to = new LatLng(order.getToAddess().getLatitude(), order.getToAddess().getLongitude());
            from = new LatLng(order.getFromAddess().getLatitude(), order.getFromAddess().getLongitude());

            sendRequest(to, from);
        }else {
            if(user.getRole_id().equals("1")) {
                newOrderView.setVisibility(View.VISIBLE);
                openSessionView.setVisibility(View.GONE);
            }else {
                newOrderView.setVisibility(View.GONE);
                openSessionView.setVisibility(View.VISIBLE);
            }

            LatLng latLng = Paper.book().read(Constants.MYLOCATION, new LatLng(0,0));
            String push_id = Paper.book().read(Constants.FIREBASE_TOKEN, "");
            checkSession(latLng, push_id);
        }
    }

    private void setListeners() {
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).drawerAction();
            }
        });

        myLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myLocation != null){
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( myLocation, 17);
                    map.animateCamera(cameraUpdate);
                }
            }
        });

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromAddress != null && toAddress != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    Bundle b = new Bundle();
                    b.putParcelable(Constants.TOADDRESS, toAddress);
                    b.putParcelable(Constants.FROMADDRESS, fromAddress);

                    SelectModeFragment selectModeFragment = new SelectModeFragment();
                    selectModeFragment.setArguments(b);

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.exit_to_top);
                    fragmentTransaction.add(R.id.main_activity_frame, selectModeFragment, SelectModeFragment.TAG);
                    fragmentTransaction.addToBackStack(SelectModeFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getContext(), getResources().getText(R.string.set_addresses), Toast.LENGTH_SHORT).show();
                }
            }
        });

        toButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.MODE, 2);

                FromAndToFragment fromFragment = new FromAndToFragment();
                fromFragment.setTargetFragment(MainFragment.this, Constants.MAINFRAGMENTCODETO);
                fromFragment.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.exit_to_top);
                fragmentTransaction.add(R.id.main_activity_frame, fromFragment, FromAndToFragment.TAG);
                fragmentTransaction.addToBackStack(FromAndToFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.MODE, 1);

                FromAndToFragment fromFragment = new FromAndToFragment();
                fromFragment.setTargetFragment(MainFragment.this, Constants.MAINFRAGMENTCODEFROM);
                fromFragment.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.exit_to_top);
                fragmentTransaction.add(R.id.main_activity_frame, fromFragment, FromAndToFragment.TAG);
                fragmentTransaction.addToBackStack(FromAndToFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        openSessionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.isSessionOpened()) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    OpenSessionFragment openSessionFragment = new OpenSessionFragment();

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.exit_to_top);
                    fragmentTransaction.replace(R.id.main_activity_frame, openSessionFragment, OpenSessionFragment.TAG);
                    fragmentTransaction.addToBackStack(OpenSessionFragment.TAG);
                    fragmentTransaction.commit();
                }else {
                    closeSession();
                }
            }
        });

        cameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    switch (driverButtonType){
                        case 0:
                            driverIsCame(orderId);
                            break;
                        case 1:
                            driverGo(orderId);
                            break;

                        case 2:
                            driverFinish(orderId);
                            break;
                    }
                }catch (Throwable e){
                    Log.d(NULLTAG,": orderId is null");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (checkGPSPermission()) {
            this.map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(true);

            if(theme == 2){
                googleMap.setMapStyle(new MapStyleOptions(getResources()
                        .getString(R.string.style_json)));
            }

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    drawMarker(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            if (location != null) {
                drawMarker(location);
            }
            setViewsVisibility();

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(isClickableA){
                        Address address = getAddressFromLatLng(latLng);
                        try {
                            String title = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
                            fromAddress = new Place(title, address.getLatitude(), address.getLongitude());
                            fromButton.setText(title);

                            if(markerFrom != null){
                                markerFrom.remove();
                            }
                            markerFrom = map.addMarker(
                                    new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_a)))
                                    .position(latLng).title(getString(R.string.point_a)));

                            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }catch (Throwable throwable){}
                    }else if(isClickableB){
                        Address address = getAddressFromLatLng(latLng);
                        try {
                            String title = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
                            toAddress = new Place(title, address.getLatitude(), address.getLongitude());
                            toButton.setText(title);

                            if(markerTo != null){
                                markerTo.remove();
                            }
                            markerTo = map.addMarker(
                                    new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_b)))
                                    .position(latLng).title(getString(R.string.point_b)));
                            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }catch (Throwable throwable){}
                    }
                }
            });

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    if(myLocation != null) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 16);
                        map.animateCamera(cameraUpdate);
                    }
                }
            });
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
        }
    }

    private void sendRequest(LatLng origin, LatLng destination) {
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
            if(order != null) {
                openModeFragment(order);
            }

            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_b)))
                    .position(to));

            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_a)))
                    .position(from));

            for (int i = 0; i < direction.getRouteList().size(); i++) {
                Route route = direction.getRouteList().get(i);
                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                map.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, getResources().getColor(R.color.colorPrimary)));
            }
            setCameraWithCoordinationBounds(direction.getRouteList().get(0));
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_GPS_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapView.getMapAsync(this);
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
                }
                return;
            }

            case REQUEST_CALL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+" + orderInfo.getDriver().getPhone()));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                }
                return;
            }
            default:
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(data.getStringExtra(Constants.FROMMAP) == null){
                if (requestCode == Constants.MAINFRAGMENTCODEFROM) {
                    fromAddress = data.getParcelableExtra("address");
                    LatLng latLng = new LatLng(fromAddress.getLatitude(), fromAddress.getLongitude());
                    if(markerFrom != null){
                        markerFrom.remove();
                    }
                    markerFrom = map.addMarker(
                            new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_a)))
                            .position(latLng).title(fromAddress.getAddress()));
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    fromButton.setText(fromAddress.getAddress());
                }else if (requestCode == Constants.MAINFRAGMENTCODETO) {
                    toAddress = data.getParcelableExtra("address");
                    LatLng latLng = new LatLng(toAddress.getLatitude(), toAddress.getLongitude());
                    if(markerTo != null){
                        markerTo.remove();
                    }
                    markerTo = map.addMarker(
                            new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_b)))
                            .position(latLng).title(toAddress.getAddress())
                    );
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    toButton.setText(toAddress.getAddress());
                }else if(requestCode == Constants.MAINFRAGMENTCODECHECKOUT){
                    map.clear();
                    newOrderView.setVisibility(View.VISIBLE);
                }
            }else {
                int mode = data.getIntExtra(Constants.MODE, 1);
                if(mode == 1){
                    isClickableA = true;
                    isClickableB = false;
                }else if (mode == 2){
                    isClickableA = false;
                    isClickableB = true;
                }
            }
        }
    }

    //requests
    private void closeSession(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .closeSession(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        if(response.getState().equals("success")){
            saveUserSession(false);
            sessionText.setText(getResources().getString(R.string.open_session_event));
            Toast.makeText(getContext(), getResources().getString(R.string.successfully_closed), Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void checkSession(LatLng latLng, String push_id){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .checkState(Utility.getToken(getContext()), push_id, latLng.latitude, latLng.longitude, "0")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCheck, this::handleErrorCheck));
    }

    private void handleResponseCheck(Response response) {
        progressBar.setVisibility(View.GONE);

        if(response.getState().equals("success")){
            if(user.getRole_id().equals("2")){
                if(response.getIs_session_opened() == 1){
                    user.setSessionOpened(true);
                    sessionText.setText(getResources().getString(R.string.close_session_event));
                }else {
                    user.setSessionOpened(false);
                    sessionText.setText(getResources().getString(R.string.open_session_event));
                }

                if(response.getIs_active() != 1){
                    ((MainActivity) Objects.requireNonNull(getActivity())).openInfoDialogView(getResources().getString(R.string.on_moderation),R.drawable.icon_error);
                }
            }
            setState(response);
            user.setBalance(response.getBalance());
            Paper.book().write(Constants.USER, user);
        }
    }

    private void handleErrorCheck(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void driverIsCame(String order_id){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .driverCame(Utility.getToken(getContext()), order_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCame, this::handleErrorCame));
    }

    private void handleResponseCame(Response response) {
        if(response.getState().equals("success")){
            driverButtonType = 1;
            cameButton.setText(getResources().getString(R.string.start_trip));
            Toast.makeText(getContext(), getResources().getString(R.string.came_button_response), Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleErrorCame(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void driverGo(String order_id){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .driverGo(Utility.getToken(getContext()), order_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGo, this::handleErrorGo));
    }

    private void handleResponseGo(Response response) {
        if(response.getState().equals("success")){
            driverButtonType = 2;
            cameButton.setText(getResources().getString(R.string.end_trip));
            Toast.makeText(getContext(), getResources().getString(R.string.trip_is_started), Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleErrorGo(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void driverFinish(String order_id){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .driverFinisg(Utility.getToken(getContext()), order_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseFinish, this::handleErrorFinish));
    }

    private void handleResponseFinish(Response response) {
        if(response.getState().equals("success")){
            driverButtonType = 0;
            cameButton.setText(getResources().getString(R.string.came_button));
            cameButton.setVisibility(View.GONE);
            Toast.makeText(getContext(), getResources().getString(R.string.trip_is_ended), Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleErrorFinish(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void getOrderInfo(String order_id){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getOrderInfo(order_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseOrderInfo, this::handleErrorOrderInfo));
    }

    private void handleResponseOrderInfo(OrderToDriver.GetOrderInfo response) {
        if(response.getState().equals("success")){
            if(view != null){
                if(!mainState.equals("1")) {
                    initViewsBottomSheet(view);
                    setInfo(response);
                }
                from = new LatLng(response.getOrder().getFrom_latitude(), response.getOrder().getFrom_longitude());
                to = new LatLng(response.getOrder().getTo_latitude(), response.getOrder().getTo_longitude());

                sendRequest(from, to);
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleErrorOrderInfo(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    // helper functions
    private void setState(Response response) {
        mainState = response.getStatus();
        orderId = response.getOrder_id();

        switch (mainState){
            case "0"://cancelled
                if(user.getRole_id().equals("1")){
                    newOrderView.setVisibility(View.VISIBLE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.GONE);
                    layoutBottomSheet.setVisibility(View.GONE);
                }else {
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.VISIBLE);
                    cameButton.setVisibility(View.GONE);
                    layoutBottomSheet.setVisibility(View.GONE);
                }
                break;

            case "1": // on waiting
                if(user.getRole_id().equals("1")){
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.GONE);

                    getOrderInfo(orderId);
                }
                break;

            case "2": // on the way to client
                if(user.getRole_id().equals("1")){
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.GONE);

                    getOrderInfo(orderId);
                }else {
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.VISIBLE);
                    layoutBottomSheet.setVisibility(View.GONE);

                    cameButton.setText(getResources().getString(R.string.came_button));
                    driverButtonType = 0;
                }
                break;

            case "3": // on waiting response
                if(user.getRole_id().equals("1")){
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.GONE);

                    getOrderInfo(orderId);
                }else {
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.VISIBLE);
                    layoutBottomSheet.setVisibility(View.GONE);

                    cameButton.setText(getResources().getString(R.string.start_trip));
                    driverButtonType = 1;
                }
                break;

            case "4": // on the way with client
                if(user.getRole_id().equals("1")){
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.GONE);

                    getOrderInfo(orderId);
                }else {
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.VISIBLE);
                    layoutBottomSheet.setVisibility(View.GONE);

                    cameButton.setText(getResources().getString(R.string.end_trip));
                    driverButtonType = 2;
                }
                break;

            case "5": // ended
                if(user.getRole_id().equals("1")){
                    newOrderView.setVisibility(View.VISIBLE);
                    openSessionView.setVisibility(View.GONE);
                    cameButton.setVisibility(View.GONE);
                    layoutBottomSheet.setVisibility(View.GONE);
                }else {
                    newOrderView.setVisibility(View.GONE);
                    openSessionView.setVisibility(View.VISIBLE);
                    cameButton.setVisibility(View.GONE);
                    layoutBottomSheet.setVisibility(View.GONE);
                    driverButtonType = 0;
                }
                break;
        }
    }

    public void clientIsAccepted(String orderId){
        this.orderId = orderId;
        cameButton.setVisibility(View.VISIBLE);
    }

    public void setCheckoutView(OrderToDriver.GetOrderInfo orderInfo){
        this.orderInfo = orderInfo;
        if(view != null && orderInfo!= null){
            initViewsBottomSheet(view);
            setInfo(orderInfo);
        }
    }

    private void initViewsBottomSheet(View view) {
        confirmFromText = view.findViewById(R.id.mf_confirm_from_text);
        confirmToText = view.findViewById(R.id.mf_confirm_to_text);

        confNameText = view.findViewById(R.id.mf_confirm_name_text);
        confPhoneText = view.findViewById(R.id.mf_confirm_phone_text);
        confNumberText = view.findViewById(R.id.mf_confirm_number_text);
        confModelText = view.findViewById(R.id.mf_confirm_model_text);

        confModeText = view.findViewById(R.id.mf_confirm_mode_text);
        confDateText = view.findViewById(R.id.mf_confirm_date_text);

        confCallButton = view.findViewById(R.id.mf_confirm_call_button);
        chatButton = view.findViewById(R.id.mf_confirm_chat_button);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        layoutBottomSheet.setVisibility(View.VISIBLE);
        confCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCallPermission()){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+" + orderInfo.getDriver().getPhone()));
                    startActivity(intent);
                }else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                }
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();

                ChatFragment chatFragment = new ChatFragment();
                fragmentTransaction.add(R.id.main_activity_frame, chatFragment, ChatFragment.TAG);
                fragmentTransaction.addToBackStack(ChatFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    private void setInfo(OrderToDriver.GetOrderInfo orderInfo) {
        this.orderInfo = orderInfo;
        confirmFromText.setText(getAddressFromLatLngStr(new LatLng(orderInfo.getOrder().getFrom_latitude(), orderInfo.getOrder().getFrom_longitude())));
        confirmToText.setText(getAddressFromLatLngStr(new LatLng(orderInfo.getOrder().getTo_latitude(), orderInfo.getOrder().getTo_longitude())));

        confNameText.setText(orderInfo.getDriver().getName());
        confPhoneText.setText(orderInfo.getDriver().getPhone());
        confNumberText.setText(orderInfo.getDriver().getCar_number());
        confModelText.setText(orderInfo.getCar());
        confModeText.setText(setOrder(orderInfo.getOrder().getOrder_type()));
        confDateText.setText(setDataString(orderInfo.getOrder().getDate()));
    }

    private void openModeFragment(NewOrder order) {
        fragmentTransaction = getFragmentManager().beginTransaction();
        ModesDialogFragment modesDialogFragment = ModesDialogFragment.newInstance(order);
        fragmentTransaction.add(R.id.main_activity_frame, modesDialogFragment, ModesDialogFragment.TAG);
        fragmentTransaction.addToBackStack(ModesDialogFragment.TAG);
        fragmentTransaction.commit();
    }

    private Bitmap setIcon(int src){
        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), src);
        return Bitmap.createScaledBitmap(icon, 125, 100, false);
    }

    private Address getAddressFromLatLng(LatLng latLng){
        List<Address> addressList = null;
        Address addresReturn = null;
        Geocoder geocoder = new Geocoder(getContext(), getResources().getConfiguration().locale);

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            addresReturn = addressList.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresReturn;
    }

    private boolean checkGPSPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void drawMarker(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        writeLocation(myLocation);
        if (drawerCounter == 0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 16);
            map.animateCamera(cameraUpdate);
        }

        if (mPositionMarker != null) {
            mPositionMarker.remove();
        }
        mPositionMarker = map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location))
                .title("Вы тут!"));

        drawerCounter += 1;

    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void writeLocation(LatLng myLocation){
        Paper.book().write(Constants.MYLOCATION, myLocation);
    }

    private void saveUserSession(boolean sessionState){
        user.setSessionOpened(sessionState);
        Paper.book().write(Constants.USER, user);
    }

    private String setOrder(String order_type) {
        String typeString = "";
        switch (order_type){
            case "1":
                typeString = getResources().getString(R.string.econom_mode);
                break;

            case "2":
                typeString = getResources().getString(R.string.comfort_mode);
                break;

            case "3":
                typeString = getResources().getString(R.string.business_mode);
                break;

            case "4":
                typeString = getResources().getString(R.string.modeLadyTaxi);
                break;

            case "5":
                typeString = getResources().getString(R.string.modeInvaTaxi);
                break;

            case "6":
                typeString = getResources().getString(R.string.modeCitiesTaxi);
                break;

            case "7":
                typeString = getResources().getString(R.string.modeCargoTaxi);
                break;

            case "8":
                typeString = getResources().getString(R.string.modeEvo);
                break;
        }
        return typeString;
    }

    private String getAddressFromLatLngStr(LatLng latLng){
        List<Address> addressList;
        Address addresReturn = null;
        String title;

        Geocoder geocoder = new Geocoder(getContext(), getResources().getConfiguration().locale);

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            addresReturn = addressList.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert addresReturn != null;
        title = addresReturn.getAddressLine(0).substring(0, addresReturn.getAddressLine(0).indexOf(","));

        return title;
    }

    private String setDataString(String miliseconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(miliseconds));
        return formatter.format(calendar.getTime());
    }

    private boolean checkCallPermission() {
        String permission = "android.permission.CALL_PHONE";
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void clearMap(){
        map.clear();
        newOrderView.setVisibility(View.VISIBLE);
        layoutBottomSheet.setVisibility(View.GONE);
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            double latitude = Double.valueOf(intent.getStringExtra(Constants.DRIVERLATITUDE));
            double longitude = Double.valueOf(intent.getStringExtra(Constants.DRIVERLONGITUDE));

            if(driverCarMarker != null){
                driverCarMarker.remove();
            }

            if(map!= null) {
                driverCarMarker = map.addMarker(
                        new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_taxi))
                                .position(new LatLng(latitude, longitude))
                                .title("Driver")
                );
            }

        }
    };
}