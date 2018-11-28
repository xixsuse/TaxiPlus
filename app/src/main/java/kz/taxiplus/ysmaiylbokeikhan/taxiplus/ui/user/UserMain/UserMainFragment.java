package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain;


import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
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

import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments.CancelOrderDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.FromAndToFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.MakeOrderFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.ModesDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.WebFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.ChatFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class UserMainFragment extends Fragment implements OnMapReadyCallback, CancelOrderDialogFragment.CancelOrderDialogFragmentListener {
    public static final String TAG = Constants.USERMAINFRAGMENTTAG;
    private static final String TAXIMODE = "taxiMode";
    public static final int REQUEST_GPS_PERMISSION = 123;
    private static final int REQUEST_CALL_PERMISSION = 103;

    private int drawerCounter = 0;
    private int taxiMode;
    private int theme;
    private boolean isClickableA = false;
    private boolean isClickableB = false;
    private String orderId;
    private LatLng myLocation;
    private LatLng to, from;
    private Place fromAddress, toAddress;
    private Marker mPositionMarker, markerTo, markerFrom, driverCarMarker;
    private User user;
    private NewOrder order;
    private Response mainResponse;
    private OrderToDriver.GetOrderInfo orderInfo;

    public MapView mapView;
    private GoogleMap map;

    private ImageView myLocationIcon, userLogo;
    private LinearLayout menuIcon;
    private ConstraintLayout newOrderView, waitingView;
    private Button fromButton, toButton, makeOrderButton, cancelButton;
    private ProgressBar progressBar;
    private BottomSheetBehavior sheetBehavior;
    private ConstraintLayout layoutBottomSheet;
    private TextView confirmFromText, confirmToText, confNameText;
    private TextView confPhoneText, confModelText, confNumberText;
    private TextView confDateText, confModeText;
    private ImageButton confCallButton,chatButton;

    private FragmentTransaction fragmentTransaction;
    private UserMainViewModel viewModel;

    public static UserMainFragment newInstance(int taxiMode) {
        UserMainFragment fragment = new UserMainFragment();
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
            order = getArguments().getParcelable(Constants.NEWORDER);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_main, container, false);
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
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(isClickableA){
                        Address address = Utility.getAddressFromLatLng(latLng,getContext());
                        try {
                            String title = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
                            fromAddress = new Place(title, address.getLatitude(), address.getLongitude());
                            fromButton.setText(title);

                            if(markerFrom != null){
                                markerFrom.remove();
                            }
                            markerFrom = map.addMarker(
                                    new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromBitmap(Utility.setIcon(R.drawable.icon_point_a, getContext())))
                                            .position(latLng).title(getString(R.string.point_a)));

                            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }catch (Throwable throwable){}
                    }else if(isClickableB){
                        Address address = Utility.getAddressFromLatLng(latLng,getContext());
                        try {
                            String title = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
                            toAddress = new Place(title, address.getLatitude(), address.getLongitude());
                            toButton.setText(title);

                            if(markerTo != null){
                                markerTo.remove();
                            }
                            markerTo = map.addMarker(
                                    new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromBitmap(Utility.setIcon(R.drawable.icon_point_b, getContext())))
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

            UserMainViewModel.MyViewModelFactory viewModelFactory = new UserMainViewModel.MyViewModelFactory(getActivity().getApplication(), getContext());
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserMainViewModel.class);
            observeResponseViewModel(viewModel);
        }else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS_PERMISSION);
        }
    }


    private void initViews(View view) {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myBroadcastReceiver,
                new IntentFilter("thisIsForMainFragment"));
        theme = Paper.book().read(getString(R.string.prefs_theme_key), 1);
        user = Paper.book().read(Constants.USER);

        menuIcon = view.findViewById(R.id.mf_menu_icon);
        myLocationIcon = view.findViewById(R.id.mf_my_location);
        progressBar = view.findViewById(R.id.mf_progressbar);

        newOrderView = view.findViewById(R.id.mf_new_order_view);
        toButton = view.findViewById(R.id.mf_to_button);
        fromButton = view.findViewById(R.id.mf_from_button);
        makeOrderButton = view.findViewById(R.id.mf_make_order);

        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
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

        cancelButton = view.findViewById(R.id.fw_cancel_button);
        waitingView = view.findViewById(R.id.waiting_view);
        userLogo = view.findViewById(R.id.fw_logo);

        if(order != null) {
            openModeFragment(order);
        }

        setListeners();
    }

    private void setListeners(){
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

        makeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromAddress != null && toAddress != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.MODE, taxiMode);
                    bundle.putParcelable(Constants.TOADDRESS, toAddress);
                    bundle.putParcelable(Constants.FROMADDRESS, fromAddress);

                    MakeOrderFragment makeOrderFragment = new MakeOrderFragment();
                    makeOrderFragment.setArguments(bundle);

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.exit_to_top);
                    fragmentTransaction.add(R.id.main_activity_frame, makeOrderFragment, MakeOrderFragment.TAG);
                    fragmentTransaction.addToBackStack(MakeOrderFragment.TAG);
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
                fromFragment.setTargetFragment(UserMainFragment.this, Constants.MAINFRAGMENTCODETO);
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
                fromFragment.setTargetFragment(UserMainFragment.this, Constants.MAINFRAGMENTCODEFROM);
                fromFragment.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.exit_to_top);
                fragmentTransaction.add(R.id.main_activity_frame, fromFragment, FromAndToFragment.TAG);
                fragmentTransaction.addToBackStack(FromAndToFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        confCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utility.checkCallPermission(getContext()) && orderInfo != null){
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
                if(orderInfo != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();

                    ChatFragment chatFragment = ChatFragment.newInstance(orderInfo.getDriver().getPhone(), orderInfo.getDriver().getName());
                    fragmentTransaction.add(R.id.main_activity_frame, chatFragment, ChatFragment.TAG);
                    fragmentTransaction.addToBackStack(ChatFragment.TAG);
                    fragmentTransaction.commit();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelOrderDialogFragment dialogFragment = CancelOrderDialogFragment.newInstance(orderId);
                dialogFragment.setTargetFragment(UserMainFragment.this, 300);
                dialogFragment.show(getFragmentManager(), CancelOrderDialogFragment.TAG);
            }
        });
    }

    //observe UserViewModel
    private void observeResponseViewModel(UserMainViewModel viewModel){
        viewModel.getResponseLiveData().observe(this, response -> {
            if(response != null && response.getState().equals("success")){
                mainResponse = response;
                setUserState(response);
            }
        });
    }

    private void observeOrderInfoViewModel(UserMainViewModel viewModel) {
        viewModel.getOrderInfoLiveData().observe(this, orderInfo -> {
            if(orderInfo != null && orderInfo.getState().equals("success")) {
                setOrderInfoView(orderInfo);
                from = new LatLng(orderInfo.getOrder().getFrom_latitude(), orderInfo.getOrder().getFrom_longitude());
                to = new LatLng(orderInfo.getOrder().getTo_latitude(), orderInfo.getOrder().getTo_longitude());
                viewModel.sendRequest(from, to);
                observeDirectionsViewModel(viewModel);
            }
        });
    }

    private void observeDirectionsViewModel(UserMainViewModel viewModel){
        viewModel.getDirection().observe(this, direction -> {
            if (direction != null && direction.isOK()){
                drawDirection(direction);
            }
        });
    }


    //helper functions
    private void setUserState(Response res){
        this.orderId = res.getOrder_id();
        switch (res.getStatus()){
            case "0":
                setCancelledState();
                break;

            case "1":
                setWaitingState(orderId, "");
                break;

            case "2":
                setWithOrderInfo(orderId);
                break;

            case "3":
                setWithOrderInfo(orderId);
                break;

            case "4":
                setWithOrderInfo(orderId);
                break;

            default:
                setCancelledState();
                break;
        }
        user.setBalance(res.getBalance());
        Paper.book().write(Constants.USER, user);
    }

    private void setCancelledState(){
        newOrderView.setVisibility(View.VISIBLE);
        waitingView.setVisibility(View.GONE);
        layoutBottomSheet.setVisibility(View.GONE);

        if (order != null) {
            to = new LatLng(order.getToAddess().getLatitude(), order.getToAddess().getLongitude());
            from = new LatLng(order.getFromAddess().getLatitude(), order.getFromAddess().getLongitude());

            viewModel.sendRequest(to, from);
            observeDirectionsViewModel(viewModel);
        }
    }

    public void setWaitingState(String orderId, String url){
        this.orderId = orderId;
        newOrderView.setVisibility(View.GONE);
        waitingView.setVisibility(View.VISIBLE);
        layoutBottomSheet.setVisibility(View.GONE);

        if(user.getAvatar_path() != null) {
            Glide.with(getContext())
                    .load(user.getAvatar_path())
                    .apply(RequestOptions.circleCropTransform())
                    .into(userLogo);
        }

        if(url != null && !url.equals("")){
            fragmentTransaction = getFragmentManager().beginTransaction();

            WebFragment webFragment = WebFragment.newInstance(url);
            webFragment.setTargetFragment(UserMainFragment.this, Constants.MAINFRAGMENTCODEWEB);

            fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.exit_to_top);
            fragmentTransaction.add(R.id.main_activity_frame, webFragment, WebFragment.TAG);
            fragmentTransaction.addToBackStack(WebFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    public void setWithOrderInfo(String orderId){
        newOrderView.setVisibility(View.GONE);
        waitingView.setVisibility(View.GONE);
        //layoutBottomSheet visible after get orderInfo

        viewModel.sentRequestToOrderInfo(orderId);
        observeOrderInfoViewModel(viewModel);
    }

    private void setOrderInfoView(OrderToDriver.GetOrderInfo orderInfo){
        this.orderInfo = orderInfo;
        try {
            confNameText.setText(orderInfo.getDriver().getName());
            confPhoneText.setText(orderInfo.getDriver().getPhone());
            confNumberText.setText(orderInfo.getCar().get(0).getNumber());
            confModelText.setText(orderInfo.getCar().get(0).getModel() + " "+ orderInfo.getCar().get(0).getSubmodel());
            confModeText.setText(Utility.setOrder(orderInfo.getOrder().getOrder_type(), getContext()));
            confDateText.setText(Utility.setDataString(orderInfo.getOrder().getDate()));

            confirmFromText.setText(Utility.getAddressFromLatLngStr(new LatLng(orderInfo.getOrder().getFrom_latitude(), orderInfo.getOrder().getFrom_longitude()), getContext()));
            confirmToText.setText(Utility.getAddressFromLatLngStr(new LatLng(orderInfo.getOrder().getTo_latitude(), orderInfo.getOrder().getTo_longitude()), getContext()));

            layoutBottomSheet.setVisibility(View.VISIBLE);
        }catch (Exception e){}
    }

    private void drawMarker(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Paper.book().write(Constants.MYLOCATION, myLocation);

        if (drawerCounter == 0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 16);
            map.animateCamera(cameraUpdate);
            setCurrentLocation(myLocation);
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

    private void setCurrentLocation(LatLng latLng){
        if(getContext()!= null) {
            Address address = Utility.getAddressFromLatLng(latLng, getContext());
            try {
                String title = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
                fromAddress = new Place(title, address.getLatitude(), address.getLongitude());
                fromButton.setText(title);
            } catch (Throwable throwable) {
            }
        }
    }

    private void openModeFragment(NewOrder order) {
        fragmentTransaction = getFragmentManager().beginTransaction();
        ModesDialogFragment modesDialogFragment = ModesDialogFragment.newInstance(order);
        fragmentTransaction.add(R.id.main_activity_frame, modesDialogFragment, ModesDialogFragment.TAG);
        fragmentTransaction.addToBackStack(ModesDialogFragment.TAG);
        fragmentTransaction.commit();
    }

    private void drawDirection(Direction direction){
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(Utility.setIcon(R.drawable.icon_point_b, getContext())))
                .position(to));

        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(Utility.setIcon(R.drawable.icon_point_a, getContext())))
                .position(from));

//        for (int i = 0; i < direction.getRouteList().size(); i++) {
//            Route route = direction.getRouteList().get(i);
//            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
//            map.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, getResources().getColor(R.color.colorPrimary)));
//        }
        Route route = direction.getRouteList().get(0);
        map.addPolyline(DirectionConverter.createPolyline(getContext(), route.getLegList().get(0).getDirectionPoint(), 7, getResources().getColor(R.color.colorPrimary)));

        setCameraWithCoordinationBounds(direction.getRouteList().get(0));
    }

    public void clearMap(){
        order = null;
        orderId = null;
        map.clear();
        setCancelledState();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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
                                    .icon(BitmapDescriptorFactory.fromBitmap(Utility.setIcon(R.drawable.icon_point_a, getContext())))
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
                                    .icon(BitmapDescriptorFactory.fromBitmap(Utility.setIcon(R.drawable.icon_point_b, getContext())))
                                    .position(latLng).title(toAddress.getAddress())
                    );
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    toButton.setText(toAddress.getAddress());
                }else if(requestCode == Constants.MAINFRAGMENTCODECHECKOUT){
                    map.clear();
                    newOrderView.setVisibility(View.VISIBLE);
                }else if(requestCode == Constants.MAINFRAGMENTCODEWEB){
                    getActivity().onBackPressed();
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

    @Override
    public void onFinishCancelDialog(boolean isSuccess) {
        if(isSuccess){
            clearMap();
        }
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            double latitude = Double.valueOf(intent.getStringExtra(Constants.DRIVERLATITUDE));
            double longitude = Double.valueOf(intent.getStringExtra(Constants.DRIVERLONGITUDE));

            if(driverCarMarker != null){
                driverCarMarker.remove();
            }

            if(map!= null && orderInfo != null) {
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
