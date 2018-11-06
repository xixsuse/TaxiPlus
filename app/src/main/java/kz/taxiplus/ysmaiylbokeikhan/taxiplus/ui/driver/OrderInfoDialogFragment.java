package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Objects;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain.UserMainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.content.Context.LOCATION_SERVICE;

public class OrderInfoDialogFragment extends DialogFragment implements OnMapReadyCallback, DirectionCallback {
    public static final String TAG = Constants.ORDERINFODIALOGFRAGMENTTAG;
    private static final String ORDER = "order";
    private static final String ORDERID = "orderid";
    public static final int REQUEST_GPS_PERMISSION = 123;

    private OrderToDriver order = new OrderToDriver();
    private LatLng from, to;

    private MapView mapView;
    private GoogleMap map;
    private Marker mPositionMarker;
    private TextView nameText, priceText, newOrderText;
    private Button acceptButton, declineButton;
    private ProgressBar progressBar;

    private LatLng myLocation;
    private int drawerCounter = 0;
    private String orderId;
    private boolean isNewOrder = false;

    private CompositeSubscription subscription;

    public static OrderInfoDialogFragment newInstance(NewOrder order) {
        OrderInfoDialogFragment fragment = new OrderInfoDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    public static OrderInfoDialogFragment newInstance(String orderId) {
        OrderInfoDialogFragment fragment = new OrderInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ORDERID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_Panel);
        if (getArguments() != null) {
            if(getArguments().getParcelable(ORDER) != null) {
                order = getArguments().getParcelable(ORDER);
            }else {
                orderId = getArguments().getString(ORDERID);
                order.setId(orderId);
                isNewOrder = true;
            }
        }
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_info_dialog, container, false);

        mapView = view.findViewById(R.id.foi_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        if (checkGPSPermission()) {
            mapView.getMapAsync(this);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
        }

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        nameText = view.findViewById(R.id.foi_name_text);
        priceText = view.findViewById(R.id.foi_price_text);
        newOrderText = view.findViewById(R.id.foi_new_order_text);
        acceptButton = view.findViewById(R.id.foi_accept_button);
        declineButton = view.findViewById(R.id.foi_decline_button);
        progressBar = view.findViewById(R.id.foi_progressbar);

        if(isNewOrder){
            newOrderText.setVisibility(View.VISIBLE);
        }

        setListeners();
    }

    private void setListeners() {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder(order.getId());
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void getFulInfo(String orderId) {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getOrderInfo(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLoc, this::handleErrorLoc));
    }

    private void handleResponseLoc(OrderToDriver.GetOrderInfo response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            to = new LatLng(response.getOrder().getTo_latitude(), response.getOrder().getTo_longitude());
            from = new LatLng(response.getOrder().getFrom_latitude(), response.getOrder().getFrom_longitude());
            sendRequest(from, to);

            nameText.setText(response.getClient().getName());
            priceText.setText(response.getOrder().getPrice() + " тг");
        }
    }

    private void handleErrorLoc(Throwable throwable){
        progressBar.setVisibility(View.GONE);
    }

    private void acceptOrder(String orderId) {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .acceptOrderDriver(Utility.getToken(getContext()), orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAccept, this::handleErrorAccept));
    }

    private void handleResponseAccept(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            getDialog().dismiss();
        }
    }

    private void handleErrorAccept(Throwable throwable){
        progressBar.setVisibility(View.GONE);
        getDialog().dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (checkGPSPermission()) {
            this.map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(true);

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
            getFulInfo(order.getId());
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
            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_b)))
                    .position(to));

            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_a)))
                    .position(from));

//            for (int i = 0; i < direction.getRouteList().size(); i++) {
//                Route route = direction.getRouteList().get(i);
//                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
//                map.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, getResources().getColor(R.color.colorPrimary)));
//            }
            Route route = direction.getRouteList().get(0);
            map.addPolyline(DirectionConverter.createPolyline(getContext(), route.getLegList().get(0).getDirectionPoint(), 7, getResources().getColor(R.color.colorPrimary)));

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
            default:
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
                break;
        }
    }

    private void drawMarker(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
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

    private boolean checkGPSPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private Bitmap setIcon(int src){
        Bitmap icon = BitmapFactory.decodeResource(Objects.requireNonNull(getContext()).getResources(), src);
        return Bitmap.createScaledBitmap(icon, 125, 100, false);
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
}
