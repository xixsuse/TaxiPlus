package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.AutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.Date;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.ResponsePrice;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain.UserMainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.PlaceArrayAdapter;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class AddSoberOrderFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{
    public static final String TAG = Constants.SOBERFRAGMENT;
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private Place fromAddress,toAddress;
    private boolean isMech = false;

    private ImageButton menuIcon;
    private EditText commentEditText;
    private TextView priceText;
    private Button makeOrderButton;
    private AutoCompleteTextView fromAutoTV, toAutoTV;
    private Switch switchTransmission;
    private ProgressBar progressBar;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private AutocompleteFilter filter;
    private CompositeSubscription subscription;

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sober_order, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        subscription = new CompositeSubscription();
        fromAutoTV = view.findViewById(R.id.fas_from_edittext);
        toAutoTV = view.findViewById(R.id.fas_to_edittext);
        priceText = view.findViewById(R.id.fas_price_text);
        commentEditText = view.findViewById(R.id.fas_comment_text);
        makeOrderButton = view.findViewById(R.id.fas_make_order_button);
        switchTransmission = view.findViewById(R.id.fas_trans_switch);
        progressBar = view.findViewById(R.id.fas_progressbar);
        menuIcon = view.findViewById(R.id.fas_menu);

        filter = new AutocompleteFilter.Builder().setCountry("KZ").build();
        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                toBounds(new LatLng(43.249940, 76.895426), 15000), filter);
        fromAutoTV.setAdapter(mPlaceArrayAdapter);
        toAutoTV.setAdapter(mPlaceArrayAdapter);
        fromAutoTV.setOnItemClickListener(mAutocompleteClickListenerFrom);
        toAutoTV.setOnItemClickListener(mAutocompleteClickListenerTo);

        setListeners();
    }

    private void setListeners() {
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        makeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toAddress != null && fromAddress !=null){
                    int kpp_type = isMech ? 2 : 1;
                    makeOrder(fromAddress, toAddress, commentEditText.getText().toString(), kpp_type);
                }else {
                    Toast.makeText(getContext(), getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchTransmission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMech = isChecked;
            }
        });
    }

    //requests
    private void makeOrder(Place fromAddres, Place toAddress, String comment, int kpp_type){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .makeOrderSobber(Utility.getToken(getContext()),fromAddres.getLatitude(),fromAddres.getLongitude(),
                        toAddress.getLatitude(), toAddress.getLongitude(),
                        5, comment, new Date().getTime(), 1, kpp_type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseMakeOrder, this::handleErrorMakeOrder));
    }

    private void handleResponseMakeOrder(Response response) {
        if (response.getState().equals("success")){
            Toast.makeText(getContext(), getResources().getString(R.string.order_added_sober), Toast.LENGTH_LONG).show();
            openMainFragment();
        }
    }

    private void handleErrorMakeOrder(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void getPrice(Place fromAddres, Place toAddress){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getSoberPrice(Utility.getToken(getContext()),fromAddres.getLatitude(),fromAddres.getLongitude(),
                        toAddress.getLatitude(), toAddress.getLongitude(),
                        "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePrice, this::handleErrorPrice));
    }

    private void handleResponsePrice(ResponsePrice responsePrice) {
        progressBar.setVisibility(View.GONE);
        if (responsePrice.getState().equals("success")){
            priceText.setText(responsePrice.getPrice() + " тг.");
        }
    }

    private void handleErrorPrice(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void openMainFragment(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        UserMainFragment userMainFragment = UserMainFragment.newInstance(1);
        fragmentTransaction.replace(R.id.main_activity_frame, userMainFragment, UserMainFragment.TAG);
        fragmentTransaction.addToBackStack(UserMainFragment.TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListenerFrom = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackFrom);
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListenerTo = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackTo);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackFrom = places -> {
        if (places.getStatus().isSuccess()) {
            final com.google.android.gms.location.places.Place place = places.get(0);
            fromAddress = new Place(place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
            fromAutoTV.setText(fromAddress.getAddress());

            if (fromAddress != null && toAddress != null){
                getPrice(fromAddress, toAddress);
            }
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackTo = places -> {
        if (places.getStatus().isSuccess()) {
            final com.google.android.gms.location.places.Place place = places.get(0);
            toAddress = new Place(place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
            toAutoTV.setText(toAddress.getAddress());

            if (fromAddress != null && toAddress != null){
                getPrice(fromAddress, toAddress);
            }
        }
    };

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }
}
