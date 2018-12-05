package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.util.ArrayList;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments.DriverComplaintDialogView;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments.InfoDialogView;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.ChatFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain.UserMainViewModel;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;

import static android.content.Context.LOCATION_SERVICE;

public class DriverMainFragment extends Fragment implements OnMapReadyCallback{
    public static final String TAG = Constants.DRIVERMAINFRAGMENTTAG;
    private static final String TAXIMODE = "taxiMode";
    public static final int REQUEST_GPS_PERMISSION = 103;
    private static final int REQUEST_CALL_PERMISSION = 104;

    private int taxiMode;
    private int theme;
    private int drawerCounter = 0;
    private int driverOptionType = 0;
    private String orderId;

    private User user;
    private Response mainResponse;
    private OrderToDriver.GetOrderInfo orderInfo;

    private ImageView myLocationIcon;
    private LinearLayout menuIcon;
    private ConstraintLayout optionsView;
    private ProgressBar progressBar;
    private ImageButton chatButton, callButton, complaintButton;
    private Button driverStateButton;

    private LatLng myLocation;
    private LatLng to, from;
    private Marker mPositionMarker;

    public MapView mapView;
    private GoogleMap map;

    private UserMainViewModel viewModel;
    private FragmentTransaction fragmentTransaction;

    public static DriverMainFragment newInstance(int taxiMode) {
        DriverMainFragment fragment = new DriverMainFragment();
        Bundle args = new Bundle();
        args.putInt(TAXIMODE, taxiMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taxiMode = getArguments().getInt(TAXIMODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_main, container, false);
        initViews(view);

        mapView = view.findViewById(R.id.mf_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        if (Utility.checkGPSPermission(getContext())) {
            mapView.getMapAsync(this);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(Utility.checkGPSPermission(getContext())){
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

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    if(myLocation != null) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 16);
                        map.animateCamera(cameraUpdate);
                    }
                }
            });

            UserMainViewModel.MyViewModelFactory viewModelFactory = new UserMainViewModel.MyViewModelFactory(getActivity().getApplication(), getContext());
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserMainViewModel.class);
            observeResponseViewModel(viewModel);
            progressBar.setVisibility(View.VISIBLE);
        }else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
        }
    }

    private void initViews(View view) {
        theme = Paper.book().read(getString(R.string.prefs_theme_key), 1);
        user = Paper.book().read(Constants.USER);

        menuIcon = view.findViewById(R.id.mf_menu_icon);
        myLocationIcon = view.findViewById(R.id.mf_my_location);
        progressBar = view.findViewById(R.id.mf_progressbar);

        optionsView = view.findViewById(R.id.mf_options_view);
        callButton = view.findViewById(R.id.mf_call_button);
        chatButton = view.findViewById(R.id.mf_chat_button);
        driverStateButton = view.findViewById(R.id.mf_came_button);
        complaintButton = view.findViewById(R.id.mf_complaint_button);

        setListeners();
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

        driverStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    switch (driverOptionType){
                        case 0:
                            viewModel.driverCame(orderId);
                            observeResponseCameViewModel(viewModel);
                            break;

                        case 1:
                            viewModel.driverGo(orderId);
                            observeResponseGoViewModel(viewModel);
                            break;

                        case 2:
                            viewModel.driverFinish(orderId);
                            observeResponseFinishViewModel(viewModel);
                            break;
                    }
                }catch (Throwable e){
                    Log.d("NULLTAG", "orderId is null");
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderInfo != null){
                    if(Utility.checkCallPermission(getContext())){
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+" + orderInfo.getClient().getPhone()));
                        startActivity(intent);
                    }else {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                    }
                }
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderInfo != null){
                    fragmentTransaction = getFragmentManager().beginTransaction();

                    ChatFragment chatFragment = ChatFragment.newInstance(orderInfo.getClient().getPhone(), orderInfo.getClient().getName());
                    fragmentTransaction.add(R.id.main_activity_frame, chatFragment, ChatFragment.TAG);
                    fragmentTransaction.addToBackStack(ChatFragment.TAG);
                    fragmentTransaction.commit();
                }
            }
        });

        complaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverComplaintDialogView complaintDialogView = new DriverComplaintDialogView();
                complaintDialogView.show(getChildFragmentManager(), DriverComplaintDialogView.TAG);
            }
        });
    }

    //observe UserViewModel
    private void observeResponseViewModel(UserMainViewModel viewModel){
        viewModel.getResponseLiveData().observe(this, response -> {
            if(response != null && response.getState().equals("success")){
                progressBar.setVisibility(View.GONE);
                mainResponse = response;
                setDriverState(response);
            }
        });
    }

    private void observeOrderInfoViewModel(UserMainViewModel viewModel) {
        viewModel.getOrderInfoLiveData().observe(this, orderInfo -> {
            if(orderInfo != null && orderInfo.getState().equals("success")) {
                this.orderInfo = orderInfo;
                orderId = orderInfo.getOrder().getId();
                optionsView.setVisibility(View.VISIBLE);

                from = new LatLng(orderInfo.getOrder().getFrom_latitude(), orderInfo.getOrder().getFrom_longitude());
                to = new LatLng(orderInfo.getOrder().getTo_latitude(), orderInfo.getOrder().getTo_longitude());
                viewModel.sendRequest(from, to);
                observeDirectionsViewModel(viewModel);
            }
        });
    }

    private void observeResponseCameViewModel(UserMainViewModel viewModel){
        viewModel.getDriderCameResponse().observe(this, response -> {
            if(response != null && response.getState().equals("success")){
                progressBar.setVisibility(View.GONE);
                setDriverStateButton(1);
                Toast.makeText(getContext(), getResources().getString(R.string.came_button_response), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void observeResponseGoViewModel(UserMainViewModel viewModel){
        viewModel.getDriderGoResponse().observe(this, response -> {
            progressBar.setVisibility(View.GONE);
            if(response != null && response.getState().equals("success")){
                setDriverStateButton(2);
                Toast.makeText(getContext(), getResources().getString(R.string.trip_is_started), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void observeResponseFinishViewModel(UserMainViewModel viewModel){
        viewModel.getDriderFinishResponse().observe(this, response -> {
            if(response != null && response.getState().equals("success")){
                progressBar.setVisibility(View.GONE);
                setDriverStateButton(0);
                clearMap();
                Toast.makeText(getContext(), getResources().getString(R.string.trip_is_ended), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void observeDirectionsViewModel(UserMainViewModel viewModel){
        progressBar.setVisibility(View.GONE);
        viewModel.getDirection().observe(this, direction -> {
            if (direction != null && direction.isOK()){
                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(Utility.setIcon(R.drawable.icon_point_b, getContext())))
                        .position(to));

                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(Utility.setIcon(R.drawable.icon_point_a, getContext())))
                        .position(from));
                Route route = direction.getRouteList().get(0);
                map.addPolyline(DirectionConverter.createPolyline(getContext(), route.getLegList().get(0).getDirectionPoint(), 7, getResources().getColor(R.color.colorPrimary)));

                setCameraWithCoordinationBounds(direction.getRouteList().get(0));
            }
        });
    }

    //helper functions
    private void setDriverState(Response res){
        setSessionState(res);

        switch (res.getStatus()){
            case "0":
                setCancelledState();
                break;

            case "2":
                setWithOrderState(res.getOrder_id(),0);
                break;

            case "3":
                setWithOrderState(res.getOrder_id(),1);
                break;

            case "4":
                setWithOrderState(res.getOrder_id(),2);
                break;

        }
    }

    private void setCancelledState(){
        optionsView.setVisibility(View.GONE);
    }

    private void setWithOrderState(String orderId, int optionType){
        this.orderId = orderId;
        setDriverStateButton(optionType);

        viewModel.sentRequestToOrderInfo(orderId);
        observeOrderInfoViewModel(viewModel);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setDriverStateButton(int driverOptionType){
        this.driverOptionType = driverOptionType;
        switch (driverOptionType){
            case 0:
                driverStateButton.setText(getResources().getString(R.string.came_button));
                break;

            case 1:
                driverStateButton.setText(getResources().getString(R.string.start_trip));
                break;

            case 2:
                driverStateButton.setText(getResources().getString(R.string.end_trip));
                break;
        }
    }

    private void setSessionState(Response response) {
        if(response.getIs_active() != 1){
            InfoDialogView infoDialogView = InfoDialogView.newInstance(getResources().getString(R.string.on_moderation),R.drawable.icon_error);
            assert getFragmentManager() != null;
            infoDialogView.show(getFragmentManager(), InfoDialogView.TAG);
        }
        user.setBalance(response.getBalance());
        Paper.book().write(Constants.USER, user);
    }

    private void drawMarker(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Paper.book().write(Constants.MYLOCATION, myLocation);

        if (drawerCounter == 0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 16);
            map.animateCamera(cameraUpdate);
        }

        if (mPositionMarker != null) {
            mPositionMarker.remove();
        }
        mPositionMarker = map.addMarker(new MarkerOptions()
                .position(currentPosition)
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

    public void clearMap(){
        map.clear();
        setCancelledState();
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
}
