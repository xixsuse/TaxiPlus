package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;
import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DriverOffer;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.OpenSessionFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.CheckoutOrderDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.FromAndToFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.ModesDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.MyPlacesFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.NewOfferDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.SelectModeFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class MainFragment extends Fragment implements OnMapReadyCallback, DirectionCallback {
    public static final String TAG = Constants.MAINFRAGMENTTAG;
    public static final int REQUEST_GPS_PERMISSION = 123;

    private LatLng myLocation;
    private int drawerCounter = 0;
    private int theme;
    private Order order;
    private User user;
    private LatLng to, from;
    private Place fromAddress, toAddress;
    private boolean isClickableA = false;
    private boolean isClickableB = false;

    private Marker mPositionMarker;
    public MapView mapView;
    private GoogleMap map;

    private ImageView myLocationIcon;
    private LinearLayout menuIcon, openSessionView;
    private ConstraintLayout newOrderView;
    private Button fromButton, toButton, modeButton;
    private TextView sessionText;
    private ProgressBar progressBar;

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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
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

        theme = Paper.book().read(getString(R.string.prefs_theme_key), 1);
        user = Paper.book().read(Constants.USER);

        if(user.isSessionOpened()){
            sessionText.setText(getResources().getString(R.string.close_session_event));
        }else {
            sessionText.setText(getResources().getString(R.string.open_session_event));
        }
        setListeners();
    }

    private void setViewsVisability() {
        if (order != null) {
            to = new LatLng(order.getToAddess().getLatitude(), order.getToAddess().getLongitude());
            from = new LatLng(order.getFromAddess().getLatitude(), order.getFromAddess().getLongitude());

            sendRequest(to, from);
        }else {
            if(user.getRole_id().equals("1")) {
                newOrderView.setVisibility(View.VISIBLE);
            }else {
                openSessionView.setVisibility(View.VISIBLE);
            }
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

//                fragmentTransaction = getFragmentManager().beginTransaction();
//                ChatFragment chatFragment = new ChatFragment();
//
//                fragmentTransaction.add(R.id.main_activity_frame, chatFragment, ChatFragment.TAG);
//                fragmentTransaction.addToBackStack(ChatFragment.TAG);
//                fragmentTransaction.commit();
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
            setViewsVisability();

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(isClickableA){
                        Address address = getAddressFromLatLng(latLng);
                        String title = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));

                        fromAddress = new Place(title, address.getLatitude(), address.getLongitude());
                        fromButton.setText(title);

                        map.clear();
                        map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_a)))
                                .position(latLng).title(getString(R.string.point_a)));
                        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }else if(isClickableB){
                        Address address = getAddressFromLatLng(latLng);
                        String title = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));

                        toAddress = new Place(title, address.getLatitude(), address.getLongitude());
                        toButton.setText(title);

                        map.clear();
                        map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_b)))
                                .position(latLng).title(getString(R.string.point_b)));
                        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
            openModeFragment(order);

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
                    map.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_a)))
                            .position(latLng).title(fromAddress.getAddress()));
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    fromButton.setText(fromAddress.getAddress());
                }else if (requestCode == Constants.MAINFRAGMENTCODETO) {
                    toAddress = data.getParcelableExtra("address");

                    LatLng latLng = new LatLng(toAddress.getLatitude(), toAddress.getLongitude());
                    map.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(setIcon(R.drawable.icon_point_b)))
                            .position(latLng).title(toAddress.getAddress()));
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    toButton.setText(toAddress.getAddress());
                }else if(requestCode == Constants.MAINFRAGMENTCODEOFFER){
                    CheckoutOrderDialogFragment checkoutOrderDialogFragment = CheckoutOrderDialogFragment.newInstance();
                    checkoutOrderDialogFragment.setTargetFragment(MainFragment.this, Constants.MAINFRAGMENTCODECHECKOUT);

                    checkoutOrderDialogFragment.show(getFragmentManager(), CheckoutOrderDialogFragment.TAG);
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

    // helper functions
    private void openModeFragment(Order order) {
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
}